package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.PostInfo;
import com.fank.f1k2.business.dao.PostInfoMapper;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IPostInfoService;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.system.domain.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PostInfoServiceImpl extends ServiceImpl<PostInfoMapper, PostInfo> implements IPostInfoService {

    private final IUserInfoService userInfoService;

    /**
     * 分页获取系统用户信息
     *
     * @param page
     * @param user
     * @return
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> selectUserPage(Page page, User user) {
        return baseMapper.selectUserPage(page, user);
    }

    // 获取模块下的贴子
    @Override
    public List<LinkedHashMap<String, Object>> getPostByTagUser(Integer tagId, Integer userId) {
        return baseMapper.getPostByTagUser(tagId, userId);
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public LinkedHashMap<String, Object> queryUserDetail(Integer userId) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("user", userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId)));
                put("post", baseMapper.queryPostByUser(userId));
            }
        };
        return result;
    }

    @Override
    public IPage<LinkedHashMap<String, Object>> postInfoByPage(Page page, PostInfo postInfo) {
        return baseMapper.postInfoByPage(page, postInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> getPostByTag(Integer tagId) {
        return baseMapper.getPostByTag(tagId);
    }

    @Override
    public LinkedHashMap<String, Object> postDetail(Integer postId) {
        return baseMapper.postDetail(postId);
    }

    @Override
    public List<LinkedHashMap<String, Object>> postByKey(String key) {
        return baseMapper.postByKey(key);
    }

    @Override
    public List<LinkedHashMap<String, Object>> recommend(Integer tagId, List<Long> collectUserIds) {
        return baseMapper.recommend(tagId, StringUtils.join(collectUserIds.toArray(), ","));
    }
}
