package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.SavingGoals;
import com.fank.f1k2.business.dao.SavingGoalsMapper;
import com.fank.f1k2.business.service.ISavingGoalsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class SavingGoalsServiceImpl extends ServiceImpl<SavingGoalsMapper, SavingGoals> implements ISavingGoalsService {

    /**
     * 分页获取存钱计划
     *
     * @param page      分页对象
     * @param queryFrom 存钱计划
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<SavingGoals> page, SavingGoals queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
