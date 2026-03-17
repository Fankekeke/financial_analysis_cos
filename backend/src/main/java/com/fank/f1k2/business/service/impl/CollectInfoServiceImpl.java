package com.fank.f1k2.business.service.impl;

import com.fank.f1k2.business.entity.CollectInfo;
import com.fank.f1k2.business.dao.CollectInfoMapper;
import com.fank.f1k2.business.service.ICollectInfoService;
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
public class CollectInfoServiceImpl extends ServiceImpl<CollectInfoMapper, CollectInfo> implements ICollectInfoService {

    @Override
    public IPage<LinkedHashMap<String, Object>> collectInfoByPage(Page page, CollectInfo collectInfo) {
        return baseMapper.collectInfoByPage(page, collectInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> collectInfoByUser(Integer userId) {
        return baseMapper.collectInfoByUser(userId);
    }
}
