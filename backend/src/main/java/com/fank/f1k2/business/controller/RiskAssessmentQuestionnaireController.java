package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.RiskAssessmentOption;
import com.fank.f1k2.business.service.IRiskAssessmentOptionService;
import com.fank.f1k2.common.exception.F1k2Exception;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentQuestionnaire;
import com.fank.f1k2.business.service.IRiskAssessmentQuestionnaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 风险评估问卷主表 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/risk-assessment-questionnaire")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RiskAssessmentQuestionnaireController {

    private final IRiskAssessmentQuestionnaireService bulletinInfoService;

    private final IRiskAssessmentOptionService riskAssessmentOptionService;

    /**
     * 分页获取风险评估问卷主表
     *
     * @param page      分页对象
     * @param queryFrom 风险评估问卷主表
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<RiskAssessmentQuestionnaire> page, RiskAssessmentQuestionnaire queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
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
    @Transactional(rollbackFor = Exception.class)
    @PostMapping
    public R save(RiskAssessmentQuestionnaire addFrom) throws F1k2Exception {
        addFrom.setQuestionnaireCode("QZ-" + System.currentTimeMillis());
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        if (StrUtil.isEmpty(addFrom.getOptionStr())) {
            throw new F1k2Exception("请选择问卷选项");
        }
        if ("1".equals(addFrom.getIsDefault())) {
            bulletinInfoService.update(Wrappers.<RiskAssessmentQuestionnaire>lambdaUpdate().set(RiskAssessmentQuestionnaire::getIsDefault, "0"));
        }
        bulletinInfoService.save(addFrom);
        List<RiskAssessmentOption> options = JSONUtil.toList(addFrom.getOptionStr(), RiskAssessmentOption.class);
        for (RiskAssessmentOption option : options) {
            option.setQuestionId(addFrom.getId());
            option.setCreatedAt(DateUtil.formatDateTime(new Date()));
        }
        return R.ok(riskAssessmentOptionService.saveBatch(options));
    }

    /**
     * 查询问卷列表
     *
     * @return 列表
     */
    @GetMapping("/queryQuestionnaireList")
    public R queryQuestionnaireList() throws F1k2Exception {
        RiskAssessmentQuestionnaire questionnaire = bulletinInfoService.getOne(Wrappers.<RiskAssessmentQuestionnaire>lambdaQuery().eq(RiskAssessmentQuestionnaire::getIsDefault, "1"));
        if (questionnaire == null) {
            throw new F1k2Exception("暂无默认问卷");
        }
        List<RiskAssessmentOption> options = riskAssessmentOptionService.list(Wrappers.<RiskAssessmentOption>lambdaQuery().eq(RiskAssessmentOption::getQuestionId, questionnaire.getId()));

        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>() {
            {
                put("questionnaire", questionnaire);
                put("options", options);
            }
        };
        return R.ok(result);
    }

    /**
     * 修改风险评估问卷主表
     *
     * @param editFrom 风险评估问卷主表对象
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    @PutMapping
    public R edit(RiskAssessmentQuestionnaire editFrom) throws F1k2Exception {
        riskAssessmentOptionService.remove(Wrappers.<RiskAssessmentOption>lambdaQuery().eq(RiskAssessmentOption::getQuestionId, editFrom.getId()));
        if (StrUtil.isEmpty(editFrom.getOptionStr())) {
            throw new F1k2Exception("请选择问卷选项");
        }
        if ("1".equals(editFrom.getIsDefault())) {
            bulletinInfoService.update(Wrappers.<RiskAssessmentQuestionnaire>lambdaUpdate().set(RiskAssessmentQuestionnaire::getIsDefault, "0"));
        }
        List<RiskAssessmentOption> options = JSONUtil.toList(editFrom.getOptionStr(), RiskAssessmentOption.class);
        for (RiskAssessmentOption option : options) {
            option.setQuestionId(editFrom.getId());
            option.setCreatedAt(DateUtil.formatDateTime(new Date()));
        }
        riskAssessmentOptionService.saveBatch(options);
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
