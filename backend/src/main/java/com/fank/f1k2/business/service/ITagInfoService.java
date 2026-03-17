package com.fank.f1k2.business.service;

import com.fank.f1k2.business.entity.TagInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK
 */
public interface ITagInfoService extends IService<TagInfo> {

    // 分页获取tag数据
    IPage<LinkedHashMap<String, Object>> tagInfoByPage(Page page, TagInfo tagInfo);
}
