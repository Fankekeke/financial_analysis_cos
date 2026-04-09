package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ProductBrowseRecord;
import com.fank.f1k2.business.dao.ProductBrowseRecordMapper;
import com.fank.f1k2.business.service.IProductBrowseRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class ProductBrowseRecordServiceImpl extends ServiceImpl<ProductBrowseRecordMapper, ProductBrowseRecord> implements IProductBrowseRecordService {

    /**
     * 分页获取理财产品浏览记录
     *
     * @param page      分页对象
     * @param queryFrom 理财产品浏览记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<ProductBrowseRecord> page, ProductBrowseRecord queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
