package com.fank.f1k2.business.controller;


import cn.hutool.core.date.DateUtil;
import com.fank.f1k2.common.utils.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fank.f1k2.business.entity.WealthManagementProduct;
import com.fank.f1k2.business.service.IWealthManagementProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

/**
 * 理财产品管理 控制层
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@RestController
@RequestMapping("/business/wealth-management-product")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WealthManagementProductController {

    private final IWealthManagementProductService bulletinInfoService;

    /**
     * 分页获取理财产品管理
     *
     * @param page      分页对象
     * @param queryFrom 理财产品管理
     * @return 结果
     */
    @GetMapping("/page")
    public R page(Page<WealthManagementProduct> page, WealthManagementProduct queryFrom) {
        return R.ok(bulletinInfoService.queryPage(page, queryFrom));
    }

    /**
     * 推荐理财产品
     *
     * @param userId 用户ID
     * @return 列表
     */
    @GetMapping("/recommendWealthManagementProduct")
    public R recommendWealthManagementProduct(Integer userId) {
        return R.ok(bulletinInfoService.recommendWealthManagementProduct(userId));
    }

    /**
     * 查询理财产品管理详情
     *
     * @param id 主键ID
     * @return 结果
     */
    @GetMapping("/{id}")
    public R detail(@PathVariable("id") Integer id) {
        return R.ok(bulletinInfoService.getById(id));
    }

    /**
     * 查询理财产品管理列表
     *
     * @return 结果
     */
    @GetMapping("/list")
    public R list() {
        return R.ok(bulletinInfoService.list());
    }

    /**
     * 新增理财产品管理
     *
     * @param addFrom 理财产品管理对象
     * @return 结果
     */
    @PostMapping
    public R save(WealthManagementProduct addFrom) {
        addFrom.setProductCode("PR-" + System.currentTimeMillis());
        addFrom.setCreatedAt(DateUtil.formatDateTime(new Date()));
        return R.ok(bulletinInfoService.save(addFrom));
    }

    /**
     * 修改理财产品管理
     *
     * @param editFrom 理财产品管理对象
     * @return 结果
     */
    @PutMapping
    public R edit(WealthManagementProduct editFrom) {
        return R.ok(bulletinInfoService.updateById(editFrom));
    }

    /**
     * 删除理财产品管理
     *
     * @param ids 删除的主键ID
     * @return 结果
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(bulletinInfoService.removeByIds(ids));
    }

}
