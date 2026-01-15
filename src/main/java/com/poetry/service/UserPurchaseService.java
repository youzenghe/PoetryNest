package com.poetry.service;

import com.poetry.common.PageResult;
import com.poetry.dto.response.UserPurchaseVO;
import com.poetry.entity.UserPurchase;
import com.poetry.mapper.UserPurchaseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserPurchaseService {

    @Autowired
    private UserPurchaseMapper purchaseMapper;

    public UserPurchaseVO mockWechatPay(Long userId, Long poemId) {
        // Check if already purchased
        int count = purchaseMapper.countByUserAndPoem(userId, poemId);
        if (count > 0) {
            throw new IllegalArgumentException("已购买过该诗词");
        }

        UserPurchase purchase = new UserPurchase();
        purchase.setUserId(userId);
        purchase.setPoemId(poemId);
        purchase.setOrderNo("WX" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        purchase.setAmount(BigDecimal.ZERO);
        purchase.setStatus("SUCCESS");
        purchase.setPayMethod("WECHAT_MINI");
        purchaseMapper.insert(purchase);

        return toVO(purchaseMapper.findById(purchase.getId()));
    }

    public List<UserPurchaseVO> getUserPurchases(Long userId) {
        return purchaseMapper.findByUserId(userId).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    public boolean checkPurchase(Long userId, Long poemId) {
        return purchaseMapper.countByUserAndPoem(userId, poemId) > 0;
    }

    public UserPurchaseVO adminCreatePurchase(Long userId, Long poemId, BigDecimal amount, LocalDateTime expiredAt) {
        int count = purchaseMapper.countByUserAndPoem(userId, poemId);
        if (count > 0) {
            throw new IllegalArgumentException("该用户已购买过该诗词");
        }

        UserPurchase purchase = new UserPurchase();
        purchase.setUserId(userId);
        purchase.setPoemId(poemId);
        purchase.setOrderNo("ADMIN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        purchase.setAmount(amount != null ? amount : BigDecimal.ZERO);
        purchase.setStatus("SUCCESS");
        purchase.setPayMethod("ADMIN_GRANT");
        purchase.setExpiredAt(expiredAt);
        purchaseMapper.insert(purchase);

        return toVO(purchaseMapper.findById(purchase.getId()));
    }

    public void cancelPurchase(Long id) {
        UserPurchase purchase = purchaseMapper.findById(id);
        if (purchase == null) {
            throw new IllegalArgumentException("购买记录不存在");
        }
        purchaseMapper.updateStatus(id, "CANCELLED");
    }

    public PageResult<UserPurchaseVO> getAllPurchases(int page, int size) {
        PageHelper.startPage(page, size);
        List<UserPurchase> purchases = purchaseMapper.findAll();
        PageInfo<UserPurchase> pageInfo = new PageInfo<>(purchases);
        List<UserPurchaseVO> voList = pageInfo.getList().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, pageInfo.getTotal(), page, size);
    }

    public List<UserPurchaseVO> getByUserId(Long userId) {
        return purchaseMapper.findByUserId(userId).stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    private UserPurchaseVO toVO(UserPurchase purchase) {
        UserPurchaseVO vo = new UserPurchaseVO();
        BeanUtils.copyProperties(purchase, vo);
        return vo;
    }
}
