package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ProjectAssets;
import com.fank.f1k2.business.dao.ProjectAssetsMapper;
import com.fank.f1k2.business.service.IProjectAssetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class ProjectAssetsServiceImpl extends ServiceImpl<ProjectAssetsMapper, ProjectAssets> implements IProjectAssetsService {

    /**
     * 分页获取产品管理
     *
     * @param page      分页对象
     * @param queryFrom 产品管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<ProjectAssets> page, ProjectAssets queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
