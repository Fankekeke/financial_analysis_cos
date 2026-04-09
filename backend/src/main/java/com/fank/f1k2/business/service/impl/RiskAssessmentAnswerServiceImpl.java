package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentAnswer;
import com.fank.f1k2.business.dao.RiskAssessmentAnswerMapper;
import com.fank.f1k2.business.service.IRiskAssessmentAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class RiskAssessmentAnswerServiceImpl extends ServiceImpl<RiskAssessmentAnswerMapper, RiskAssessmentAnswer> implements IRiskAssessmentAnswerService {

    /**
     * 分页获取用户风险评估答题记录
     *
     * @param page      分页对象
     * @param queryFrom 用户风险评估答题记录
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<RiskAssessmentAnswer> page, RiskAssessmentAnswer queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
