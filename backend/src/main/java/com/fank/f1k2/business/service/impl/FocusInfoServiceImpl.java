package com.fank.f1k2.business.service.impl;

import com.fank.f1k2.business.entity.FocusInfo;
import com.fank.f1k2.business.dao.FocusInfoMapper;
import com.fank.f1k2.business.service.IFocusInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
@Service
public class FocusInfoServiceImpl extends ServiceImpl<FocusInfoMapper, FocusInfo> implements IFocusInfoService {

    @Override
    public IPage<LinkedHashMap<String, Object>> focusInfoByPage(Page page, FocusInfo focusInfo) {
        return baseMapper.focusInfoByPage(page, focusInfo);
    }

    @Override
    public List<LinkedHashMap<String, Object>> focusInfoByUser(Integer userId) {
        return baseMapper.focusInfoByUser(userId);
    }

    /**
     * 分页查询粉丝信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<LinkedHashMap<String, Object>> focusFansInfoByUser(Integer userId) {
        return baseMapper.focusFansInfoByUser(userId);
    }
}
