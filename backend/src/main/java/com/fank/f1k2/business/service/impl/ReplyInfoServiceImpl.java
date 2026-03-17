package com.fank.f1k2.business.service.impl;

import com.fank.f1k2.business.entity.ReplyInfo;
import com.fank.f1k2.business.dao.ReplyInfoMapper;
import com.fank.f1k2.business.service.IReplyInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
public class ReplyInfoServiceImpl extends ServiceImpl<ReplyInfoMapper, ReplyInfo> implements IReplyInfoService {

    @Override
    public IPage<LinkedHashMap<String, Object>> replyInfoByPage(Page page, ReplyInfo replyInfo) {
        return baseMapper.replyInfoByPage(page, replyInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> replyListByPostId(Integer postId) {
        return baseMapper.replyListByPostId(postId);
    }
}
