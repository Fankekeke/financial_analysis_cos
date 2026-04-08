package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentQuestionnaire;
import com.fank.f1k2.business.service.IRiskAssessmentQuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 风险评估问卷主表 控制层
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/risk-assessment-questionnaire")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RiskAssessmentQuestionnaireController {

    private final IRiskAssessmentQuestionnaireService bulletinInfoService;

    /**
    * 分页获取风险评估问卷主表
    *
    * @param page       分页对象
    * @param queryFrom 风险评估问卷主表
    * @return 结果
    */
    @GetMapping("/page")
    public R page(Page<RiskAssessmentQuestionnaire> page, RiskAssessmentQuestionnaire queryFrom) {
        return R.ok();
    }

    /**
    * 查询风险评估问卷主表详情
    *
    * @param id 主键ID
    * @return 结果
    */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
    * 查询风险评估问卷主表列表
    *
    * @return 结果
    */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
    * 新增风险评估问卷主表
    *
    * @param addFrom 风险评估问卷主表对象
    * @return 结果
    */
    @PostMapping
    public R save(RiskAssessmentQuestionnaire addFrom) {
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
    * 修改风险评估问卷主表
    *
    * @param editFrom 风险评估问卷主表对象
    * @return 结果
    */
    @PutMapping
    public R edit(RiskAssessmentQuestionnaire editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
    * 删除风险评估问卷主表
    *
    * @param ids 删除的主键ID
    * @return 结果
    */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
