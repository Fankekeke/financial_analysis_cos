package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentAnswerDetail;
import com.fank.f1k2.business.dao.RiskAssessmentAnswerDetailMapper;
import com.fank.f1k2.business.service.IRiskAssessmentAnswerDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class RiskAssessmentAnswerDetailServiceImpl extends ServiceImpl<RiskAssessmentAnswerDetailMapper, RiskAssessmentAnswerDetail> implements IRiskAssessmentAnswerDetailService {

    /**
     * 分页获取用户风险评估答题详情
     *
     * @param page      分页对象
     * @param queryFrom 用户风险评估答题详情
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<RiskAssessmentAnswerDetail> page, RiskAssessmentAnswerDetail queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
