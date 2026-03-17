package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ProjectAssets;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IProjectAssetsService extends IService<ProjectAssets> {

    /**
     * 分页获取产品管理
     *
     * @param page      分页对象
     * @param queryFrom 产品管理
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<ProjectAssets> page, ProjectAssets queryFrom);
}
