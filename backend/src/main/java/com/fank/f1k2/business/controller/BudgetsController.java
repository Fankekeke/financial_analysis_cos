package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Budgets;
import com.fank.f1k2.business.service.IBudgetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 预算管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/budgets")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BudgetsController {

    private final IBudgetsService bulletinInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取预算管理
     *
     * @param page      分页对象
     * @param queryFrom 预算管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<Budgets> page, Budgets queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询预算管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询预算管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增预算管理
     *
     * @param addFrom 预算管理对象
     * @return 结果
     */
    @PostMapping
    public R save(Budgets addFrom) throws F1k2Exception {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        if (userInfo == null) {
            throw new F1k2Exception("用户不存在");
        }
        addFrom.setUserId(Long.valueOf(userInfo.getId()));

        // 添加：根据用户ID和预算周期校验是否存在
        String currentPeriod = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Budgets> existingBudgets = bulletinInfoService.list(Wrappers.<Budgets>lambdaQuery()
                .eq(Budgets::getUserId, userInfo.getId())
                .eq(Budgets::getPeriod, currentPeriod));

        if (!existingBudgets.isEmpty()) {
            throw new F1k2Exception("该预算周期已存在，无法重复创建");
        }

        boolean result = bulletinInfoService.save(addFrom);
        return R.ok(result);
    }

    /**
     * 修改预算管理
     *
     * @param editFrom 预算管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(Budgets editFrom) throws F1k2Exception {
        // 获取原始预算信息
        Budgets originalBudget = bulletinInfoService.getById(editFrom.getId());
        if (originalBudget == null) {
            throw new F1k2Exception("预算不存在");
        }

        // 获取用户信息
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, editFrom.getUserId()));
        if (userInfo == null) {
            throw new F1k2Exception("用户不存在");
        }
        editFrom.setUserId(Long.valueOf(userInfo.getId()));

        // 如果预算周期发生变化，需要校验新周期是否已存在
        if (!originalBudget.getPeriod().equals(editFrom.getPeriod())) {
            List<Budgets> existingBudgets = bulletinInfoService.list(Wrappers.<Budgets>lambdaQuery()
                    .eq(Budgets::getUserId, userInfo.getId())
                    .eq(Budgets::getPeriod, editFrom.getPeriod()));

            if (!existingBudgets.isEmpty()) {
                throw new F1k2Exception("该预算周期已存在，无法重复创建");
            }
        }

        boolean result = bulletinInfoService.updateById(editFrom);
        return R.ok(result);
    }

    /**
     * 删除预算管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
