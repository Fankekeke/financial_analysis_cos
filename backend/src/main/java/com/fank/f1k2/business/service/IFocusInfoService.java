package com.fank.f1k2.business.service;

import com.fank.f1k2.business.entity.FocusInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK
 */
public interface IFocusInfoService extends IService<FocusInfo> {

    // 分页查询关注人信息
    IPage<LinkedHashMap<String, Object>> focusInfoByPage(Page page, FocusInfo focusInfo);

    // 查询关注人信息
    List<LinkedHashMap<String, Object>> focusInfoByUser(Integer userId);

    /**
     * 分页查询粉丝信息
     *
     * @param userId
     * @return
     */
    List<LinkedHashMap<String, Object>> focusFansInfoByUser(Integer userId);
}
