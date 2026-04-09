package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentAnswerDetail;
import com.fank.f1k2.business.service.IRiskAssessmentAnswerDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 用户风险评估答题详情 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/risk-assessment-answer-detail")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RiskAssessmentAnswerDetailController {

    private final IRiskAssessmentAnswerDetailService bulletinInfoService;

    /**
     * 分页获取用户风险评估答题详情
     *
     * @param page      分页对象
     * @param queryFrom 用户风险评估答题详情
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<RiskAssessmentAnswerDetail> page, RiskAssessmentAnswerDetail queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询用户风险评估答题详情详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询用户风险评估答题详情列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增用户风险评估答题详情
     *
     * @param addFrom 用户风险评估答题详情对象
     * @return 结果
     */
    @PostMapping
    public R save(RiskAssessmentAnswerDetail addFrom) {
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改用户风险评估答题详情
     *
     * @param editFrom 用户风险评估答题详情对象
     * @return 结果
     */
    @PutMapping
    public R edit(RiskAssessmentAnswerDetail editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除用户风险评估答题详情
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
