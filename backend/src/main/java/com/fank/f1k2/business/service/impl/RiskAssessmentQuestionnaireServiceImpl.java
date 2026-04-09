package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentQuestionnaire;
import com.fank.f1k2.business.dao.RiskAssessmentQuestionnaireMapper;
import com.fank.f1k2.business.service.IRiskAssessmentQuestionnaireService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class RiskAssessmentQuestionnaireServiceImpl extends ServiceImpl<RiskAssessmentQuestionnaireMapper, RiskAssessmentQuestionnaire> implements IRiskAssessmentQuestionnaireService {

    /**
     * 分页获取风险评估问卷主表
     *
     * @param page      分页对象
     * @param queryFrom 风险评估问卷主表
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<RiskAssessmentQuestionnaire> page, RiskAssessmentQuestionnaire queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
