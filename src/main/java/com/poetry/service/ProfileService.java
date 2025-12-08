package com.poetry.service;

import com.poetry.dto.request.ProfileUpdateRequest;
import com.poetry.dto.response.UserProfileVO;
import com.poetry.entity.FavoritePoet;
import com.poetry.entity.Poem;
import com.poetry.entity.User;
import com.poetry.entity.UserProfile;
import com.poetry.mapper.FavoritePoetMapper;
import com.poetry.mapper.PoemMapper;
import com.poetry.mapper.UserMapper;
import com.poetry.mapper.UserProfileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfileService {

    @Autowired
    private UserProfileMapper profileMapper;

    @Autowired
    private FavoritePoetMapper favoritePoetMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PoemMapper poemMapper;

    public UserProfileVO getProfile(Long userId) {
        UserProfile profile = getOrCreateProfile(userId);
        return toVO(profile, userId);
    }

    @Transactional
    public UserProfileVO updateProfile(Long userId, ProfileUpdateRequest request) {
        UserProfile profile = getOrCreateProfile(userId);

        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getLocation() != null) profile.setLocation(request.getLocation());
        if (request.getBirthDate() != null) {
            profile.setBirthDate(LocalDate.parse(request.getBirthDate()));
        }
        if (request.getPreferredDynasty() != null) profile.setPreferredDynasty(request.getPreferredDynasty());
        if (request.getFavoriteStyle() != null) profile.setFavoriteStyle(request.getFavoriteStyle());
        if (request.getFavoritePoemId() != null) profile.setFavoritePoemId(request.getFavoritePoemId());

        profileMapper.update(profile);

        // Update favorite poets
        if (request.getFavoritePoets() != null) {
            favoritePoetMapper.deleteByProfileId(profile.getId());
            for (String poetName : request.getFavoritePoets()) {
                FavoritePoet fp = new FavoritePoet();
                fp.setUserProfileId(profile.getId());
                fp.setPoetName(poetName);
                favoritePoetMapper.insert(fp);
            }
        }

        return toVO(profile, userId);
    }

    /**
     * 更新头像
     */
    public void updateAvatar(Long userId, String avatarUrl) {
        UserProfile profile = getOrCreateProfile(userId);
        profile.setAvatar(avatarUrl);
        profileMapper.update(profile);
    }

    private UserProfile getOrCreateProfile(Long userId) {
        UserProfile profile = profileMapper.findByUserId(userId);
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(userId);
            profile.setAvatar("");
            profile.setBio("");
            profile.setLocation("");
            profile.setPreferredDynasty("");
            profile.setFavoriteStyle("");
            profile.setPoemsReadCount(0);
            profile.setLoginDays(0);
            profileMapper.insert(profile);
        }
        return profile;
    }

    private UserProfileVO toVO(UserProfile profile, Long userId) {
        UserProfileVO vo = new UserProfileVO();
        vo.setUserId(userId);

        User user = userMapper.findById(userId);
        if (user != null) vo.setUsername(user.getUsername());

        vo.setAvatar(profile.getAvatar());
        vo.setBio(profile.getBio());
        vo.setLocation(profile.getLocation());
        vo.setBirthDate(profile.getBirthDate());
        vo.setPreferredDynasty(profile.getPreferredDynasty());
        vo.setFavoriteStyle(profile.getFavoriteStyle());
        vo.setFavoritePoemId(profile.getFavoritePoemId());
        vo.setPoemsReadCount(profile.getPoemsReadCount());
        vo.setLoginDays(profile.getLoginDays());
        vo.setCreatedAt(profile.getCreatedAt());

        if (profile.getFavoritePoemId() != null) {
            Poem poem = poemMapper.findById(profile.getFavoritePoemId());
            if (poem != null) vo.setFavoritePoemTitle(poem.getTitle());
        }

        List<FavoritePoet> poets = favoritePoetMapper.findByProfileId(profile.getId());
        vo.setFavoritePoets(poets.stream().map(FavoritePoet::getPoetName).collect(Collectors.toList()));

        return vo;
    }
}
