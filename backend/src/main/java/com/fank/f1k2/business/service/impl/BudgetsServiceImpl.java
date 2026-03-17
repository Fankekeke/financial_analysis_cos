package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Budgets;
import com.fank.f1k2.business.dao.BudgetsMapper;
import com.fank.f1k2.business.service.IBudgetsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class BudgetsServiceImpl extends ServiceImpl<BudgetsMapper, Budgets> implements IBudgetsService {

    /**
     * 分页获取预算管理
     *
     * @param page      分页对象
     * @param queryFrom 预算管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<Budgets> page, Budgets queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
