package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Accounts;
import com.fank.f1k2.business.dao.AccountsMapper;
import com.fank.f1k2.business.service.IAccountsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class AccountsServiceImpl extends ServiceImpl<AccountsMapper, Accounts> implements IAccountsService {

    /**
     * 分页获取存款账户信息
     *
     * @param page      分页对象
     * @param queryFrom 存款账户信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<Accounts> page, Accounts queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
