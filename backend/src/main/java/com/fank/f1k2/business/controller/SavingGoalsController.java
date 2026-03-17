package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.SavingGoals;
import com.fank.f1k2.business.service.ISavingGoalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public R save(SavingGoals addFrom) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(Long.valueOf(userInfo.getId()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改存钱计划
     *
     * @param editFrom 存钱计划对象
     * @return 结果
     */
    @PutMapping
    public R edit(SavingGoals editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
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
