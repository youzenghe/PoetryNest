package com.poetry.service;

import com.poetry.common.PageResult;
import com.poetry.dto.response.AdminUserVO;
import com.poetry.dto.response.UserPurchaseVO;
import com.poetry.entity.User;
import com.poetry.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPurchaseService purchaseService;

    public PageResult<AdminUserVO> getAllUsers(int page, int size) {
        PageHelper.startPage(page, size);
        List<User> users = userMapper.findAllPaged();
        PageInfo<User> pageInfo = new PageInfo<>(users);
        List<AdminUserVO> voList = pageInfo.getList().stream()
                .map(this::toVO)
                .collect(Collectors.toList());
        return new PageResult<>(voList, pageInfo.getTotal(), page, size);
    }

    public AdminUserVO getUserDetail(Long userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return toVO(user);
    }

    public void updateUserRole(Long userId, String role) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        userMapper.updateRole(userId, role);
    }

    private AdminUserVO toVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
