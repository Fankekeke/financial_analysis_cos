package com.fank.f1k2.business.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.WealthManagementProduct;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface IWealthManagementProductService extends IService<WealthManagementProduct> {

    /**
     * 分页获取理财产品管理
     *
     * @param page      分页对象
     * @param queryFrom 理财产品管理
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<WealthManagementProduct> page, WealthManagementProduct queryFrom);

    /**
     * 推荐理财产品
     *
     * @param userId 用户ID
     * @return 列表
     */
    List<WealthManagementProduct> recommendWealthManagementProduct(Integer userId);
}
