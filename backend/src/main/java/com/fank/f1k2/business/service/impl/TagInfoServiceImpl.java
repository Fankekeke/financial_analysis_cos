package com.fank.f1k2.business.service.impl;

import com.fank.f1k2.business.entity.TagInfo;
import com.fank.f1k2.business.dao.TagInfoMapper;
import com.fank.f1k2.business.service.ITagInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
@Service
public class TagInfoServiceImpl extends ServiceImpl<TagInfoMapper, TagInfo> implements ITagInfoService {

    @Override
    public IPage<LinkedHashMap<String, Object>> tagInfoByPage(Page page, TagInfo tagInfo) {
        return baseMapper.tagInfoByPage(page, tagInfo);
    }
}
