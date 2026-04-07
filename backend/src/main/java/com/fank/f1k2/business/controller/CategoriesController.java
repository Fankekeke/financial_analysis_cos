package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fank.f1k2.business.entity.UserInfo;
import com.fank.f1k2.business.service.IUserInfoService;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.Categories;
import com.fank.f1k2.business.service.ICategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 分类与标签 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/categories")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CategoriesController {

    private final ICategoriesService bulletinInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页获取分类与标签
     *
     * @param page      分页对象
     * @param queryFrom 分类与标签
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<Categories> page, Categories queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 查询分类与标签列表
     *
     * @return 列表
     */
    @GetMapping("/queryCategoryListByUserId")
    public R queryCategoryListByUserId(Integer userId) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, userId));
    	return R.ok(bulletinInfoService.list(Wrappers.<Categories>lambdaQuery().eq(Categories::getUserId, userInfo.getId())));
    }

    /**
     * 查询分类与标签详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询分类与标签列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增分类与标签
     *
     * @param addFrom 分类与标签对象
     * @return 结果
     */
    @PostMapping
    public R save(Categories addFrom) {
        UserInfo userInfo = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, addFrom.getUserId()));
        addFrom.setUserId(Long.valueOf(userInfo.getId()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改分类与标签
     *
     * @param editFrom 分类与标签对象
     * @return 结果
     */
    @PutMapping
    public R edit(Categories editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除分类与标签
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
