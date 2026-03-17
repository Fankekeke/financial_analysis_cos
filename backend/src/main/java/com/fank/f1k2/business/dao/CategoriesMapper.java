package com.fank.f1k2.business.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Categories;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.LinkedHashMap;

/**
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
public interface CategoriesMapper extends BaseMapper<Categories> {

    /**
     * 分页获取分类与标签
     *
     * @param page      分页对象
     * @param queryFrom 分类与标签
     * @return 结果
     */
    IPage<LinkedHashMap<String, Object>> queryPage(Page<Categories> page, @Param("queryFrom") Categories queryFrom);
}
