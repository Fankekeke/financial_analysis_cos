package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Debts;
import com.fank.f1k2.business.dao.DebtsMapper;
import com.fank.f1k2.business.service.IDebtsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class DebtsServiceImpl extends ServiceImpl<DebtsMapper, Debts> implements IDebtsService {

    /**
     * 分页获取负债管理
     *
     * @param page      分页对象
     * @param queryFrom 负债管理
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<Debts> page, Debts queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
