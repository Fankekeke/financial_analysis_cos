package com.fank.f1k2.business.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.RiskAssessmentOption;
import com.fank.f1k2.business.dao.RiskAssessmentOptionMapper;
import com.fank.f1k2.business.service.IRiskAssessmentOptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Service
public class RiskAssessmentOptionServiceImpl extends ServiceImpl<RiskAssessmentOptionMapper, RiskAssessmentOption> implements IRiskAssessmentOptionService {

    /**
     * 分页获取风险评估问卷选项表
     *
     * @param page      分页对象
     * @param queryFrom 风险评估问卷选项表
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryPage(Page<RiskAssessmentOption> page, RiskAssessmentOption queryFrom) {
        return baseMapper.queryPage(page, queryFrom);
    }
}
