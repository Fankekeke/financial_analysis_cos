package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.FixedAssets;
import com.fank.f1k2.business.dao.FixedAssetsMapper;
import com.fank.f1k2.business.service.IFixedAssetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class FixedAssetsServiceImpl extends ServiceImpl<FixedAssetsMapper, FixedAssets> implements IFixedAssetsService {

    /**
     * 分页获取固定资产
     *
     * @param page      分页对象
     * @param queryFrom 固定资产
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<FixedAssets> page, FixedAssets queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
