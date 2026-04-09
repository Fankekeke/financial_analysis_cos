package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.ProductIncomeRecord;
import com.fank.f1k2.business.dao.ProductIncomeRecordMapper;
import com.fank.f1k2.business.service.IProductIncomeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class ProductIncomeRecordServiceImpl extends ServiceImpl<ProductIncomeRecordMapper, ProductIncomeRecord> implements IProductIncomeRecordService {

    /**
     * 分页获取理财产品历史收益记录
     *
     * @param page      分页对象
     * @param queryFrom 理财产品历史收益记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<ProductIncomeRecord> page, ProductIncomeRecord queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
