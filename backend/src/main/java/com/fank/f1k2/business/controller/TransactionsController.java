package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.Accounts;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IAccountsService;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Transactions;
import com.fank.f1k2.business.service.ITransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 交易记录 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/transactions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TransactionsController {

    private final ITransactionsService bulletinInfoService;

    private final IUserInfoService userInfoService;

    private final IAccountsService accountsService;

    /**
     * 分页获取交易记录
     *
     * @param page      分页对象
     * @param queryFrom 交易记录
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<Transactions> page, Transactions queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询交易记录详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询交易记录列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增交易记录
     *
     * @param addFrom 交易记录对象
     * @return 结果
     */
    @PostMapping
    public R save(Transactions addFrom) throws F1k2Exception {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        if (userInfo == null) {
            throw new F1k2Exception("用户不存在");
        }
        addFrom.setUserId(Long.valueOf(userInfo.getId()));
        addFrom.setCreatedAt(LocalDateTime.now());
        addFrom.setTransactionTime(LocalDateTime.now());

        // 校验交易金额
        if (addFrom.getAmount() == null || addFrom.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new F1k2Exception("交易金额必须大于0");
        }

        // 校验是否有关联账户
        if (addFrom.getAccountId() == null) {
            throw new F1k2Exception("请指定关联账户");
        }

        // 获取关联账户
        Accounts account = accountsService.getById(addFrom.getAccountId());
        if (account == null) {
            throw new F1k2Exception("关联账户不存在");
        }

        // 根据交易类型更新账户余额
        BigDecimal newBalance = account.getBalance();
        if (newBalance == null) {
            newBalance = BigDecimal.ZERO;
        }

        if ("收入".equals(addFrom.getTransactionType())) {
            // 收入：增加余额
            newBalance = newBalance.add(addFrom.getAmount());
        } else if ("支出".equals(addFrom.getTransactionType()) || "转账".equals(addFrom.getTransactionType())) {
            // 支出：减少余额
            if (newBalance.compareTo(addFrom.getAmount()) < 0) {
                throw new F1k2Exception("账户余额不足，无法完成支出");
            }
            newBalance = newBalance.subtract(addFrom.getAmount());
        } else {
            throw new F1k2Exception("不支持的交易类型");
        }

        // 更新账户余额
        account.setBalance(newBalance);
        boolean accountUpdated = accountsService.updateById(account);
        if (!accountUpdated) {
            throw new F1k2Exception("更新账户余额失败");
        }

        // 保存交易记录
        boolean transactionSaved = bulletinInfoService.save(addFrom);
        return R.ok(transactionSaved);
    }

    /**
     * 修改交易记录
     *
     * @param editFrom 交易记录对象
     * @return 结果
     */
    @PutMapping
    public R edit(Transactions editFrom) throws F1k2Exception {
        // 获取原始交易记录
        Transactions originalTransaction = bulletinInfoService.getById(editFrom.getId());
        if (originalTransaction == null) {
            throw new F1k2Exception("交易记录不存在");
        }

        // 获取用户信息
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, editFrom.getUserId()));
        if (userInfo == null) {
            throw new F1k2Exception("用户不存在");
        }
        editFrom.setUserId(Long.valueOf(userInfo.getId()));

        // 获取关联账户
        Accounts account = accountsService.getById(editFrom.getAccountId());
        if (account == null) {
            throw new F1k2Exception("关联账户不存在");
        }

        // 获取当前账户余额
        BigDecimal currentBalance = account.getBalance();
        if (currentBalance == null) {
            currentBalance = BigDecimal.ZERO;
        }

        // 撤销原交易对余额的影响
        if ("收入".equals(originalTransaction.getTransactionType())) {
            // 原为收入：减少余额
            currentBalance = currentBalance.subtract(originalTransaction.getAmount());
        } else if ("支出".equals(originalTransaction.getTransactionType()) || "转账".equals(originalTransaction.getTransactionType())) {
            // 原为支出：增加余额
            currentBalance = currentBalance.add(originalTransaction.getAmount());
        }

        // 校验新交易金额
        if (editFrom.getAmount() == null || editFrom.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new F1k2Exception("交易金额必须大于0");
        }

        // 应用新交易对余额的影响
        if ("收入".equals(editFrom.getTransactionType())) {
            // 新为收入：增加余额
            currentBalance = currentBalance.add(editFrom.getAmount());
        } else if ("支出".equals(editFrom.getTransactionType()) || "转账".equals(editFrom.getTransactionType())) {
            // 新为支出：减少余额
            if (currentBalance.compareTo(editFrom.getAmount()) < 0) {
                throw new F1k2Exception("账户余额不足，无法完成支出");
            }
            currentBalance = currentBalance.subtract(editFrom.getAmount());
        } else {
            throw new F1k2Exception("不支持的交易类型");
        }

        // 更新账户余额
        account.setBalance(currentBalance);
        boolean accountUpdated = accountsService.updateById(account);
        if (!accountUpdated) {
            throw new F1k2Exception("更新账户余额失败");
        }

        // 更新交易记录
        boolean transactionUpdated = bulletinInfoService.updateById(editFrom);
        return R.ok(transactionUpdated);
    }

    /**
     * 删除交易记录
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
