package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Debts;
import com.fank.f1k2.business.service.IDebtsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 负债管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/debts")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DebtsController {

    private final IDebtsService bulletinInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取负债管理
     *
     * @param page      分页对象
     * @param queryFrom 负债管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<Debts> page, Debts queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询负债管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询负债管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增负债管理
     *
     * @param addFrom 负债管理对象
     * @return 结果
     */
    @PostMapping
    public R save(Debts addFrom) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(Long.valueOf(userInfo.getId()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改负债管理
     *
     * @param editFrom 负债管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(Debts editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除负债管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
