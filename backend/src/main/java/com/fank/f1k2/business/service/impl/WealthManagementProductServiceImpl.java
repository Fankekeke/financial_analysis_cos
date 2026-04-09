package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.WealthManagementProduct;
import com.fank.f1k2.business.dao.WealthManagementProductMapper;
import com.fank.f1k2.business.service.IWealthManagementProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class WealthManagementProductServiceImpl extends ServiceImpl<WealthManagementProductMapper, WealthManagementProduct> implements IWealthManagementProductService {

    /**
     * 分页获取理财产品管理
     *
     * @param page      分页对象
     * @param queryFrom 理财产品管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<WealthManagementProduct> page, WealthManagementProduct queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
