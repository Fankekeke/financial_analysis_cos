package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.SavingGoals;
import com.fank.f1k2.business.service.ISavingGoalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 存钱计划 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/saving-goals")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SavingGoalsController {

    private final ISavingGoalsService bulletinInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取存钱计划
     *
     * @param page      分页对象
     * @param queryFrom 存钱计划
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<SavingGoals> page, SavingGoals queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询存钱计划详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询存钱计划列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增存钱计划
     *
     * @param addFrom 存钱计划对象
     * @return 结果
     */
    @PostMapping
    public R save(SavingGoals addFrom) throws F1k2Exception {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        if (userInfo == null) {
            throw new F1k2Exception("用户不存在");
        }
        addFrom.setUserId(Long.valueOf(userInfo.getId()));

        // 校验开始时间和结束时间
        if (addFrom.getStartDate() == null || addFrom.getEndDate() == null) {
            throw new F1k2Exception("开始时间和结束时间不能为空");
        }

        if (addFrom.getTargetAmount() == null || addFrom.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new F1k2Exception("目标金额必须大于0");
        }

        try {
            LocalDate start = LocalDate.parse(addFrom.getStartDate());
            LocalDate end = LocalDate.parse(addFrom.getEndDate());

            if (!end.isAfter(start)) {
                throw new F1k2Exception("结束时间必须晚于开始时间");
            }

            // 计算月份数（向上取整）
            long months = java.time.temporal.ChronoUnit.MONTHS.between(
                    start.withDayOfMonth(1),
                    end.withDayOfMonth(1).plusMonths(1)
            );

            if (months < 1) {
                months = 1;
            }

            // 计算每月建议存款金额
            BigDecimal monthlySuggestion = addFrom.getTargetAmount()
                    .divide(new BigDecimal(months), 2, BigDecimal.ROUND_HALF_UP);

            addFrom.setMonthlySuggestion(monthlySuggestion);

        } catch (Exception e) {
            throw new F1k2Exception("日期格式错误，请使用YYYY-MM-DD格式");
        }

        boolean result = bulletinInfoService.save(addFrom);
        return R.ok(result);
    }

    /**
     * 修改存钱计划
     *
     * @param editFrom 存钱计划对象
     * @return 结果
     */
    @PutMapping
    public R edit(SavingGoals editFrom) throws F1k2Exception {
        // 校验开始时间和结束时间
        if (editFrom.getStartDate() == null || editFrom.getEndDate() == null) {
            throw new F1k2Exception("开始时间和结束时间不能为空");
        }

        if (editFrom.getTargetAmount() == null || editFrom.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new F1k2Exception("目标金额必须大于0");
        }

        try {
            LocalDate start = LocalDate.parse(editFrom.getStartDate());
            LocalDate end = LocalDate.parse(editFrom.getEndDate());

            if (!end.isAfter(start)) {
                throw new F1k2Exception("结束时间必须晚于开始时间");
            }

            // 计算月份数（向上取整）
            long months = java.time.temporal.ChronoUnit.MONTHS.between(
                    start.withDayOfMonth(1),
                    end.withDayOfMonth(1).plusMonths(1)
            );

            if (months < 1) {
                months = 1;
            }

            // 计算每月建议存款金额
            BigDecimal monthlySuggestion = editFrom.getTargetAmount()
                    .divide(new BigDecimal(months), 2, BigDecimal.ROUND_HALF_UP);

            editFrom.setMonthlySuggestion(monthlySuggestion);

        } catch (Exception e) {
            throw new F1k2Exception("日期格式错误，请使用YYYY-MM-DD格式");
        }

        boolean result = bulletinInfoService.updateById(editFrom);
        return R.ok(result);
    }

    /**
     * 删除存钱计划
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
