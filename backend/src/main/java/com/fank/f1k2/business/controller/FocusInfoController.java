package com.fank.f1k2.business.controller;


import com.fank.f1k2.common.utils.R;
import com.fank.f1k2.business.entity.CollectInfo;
import com.fank.f1k2.business.entity.FocusInfo;
import com.fank.f1k2.business.service.IFocusInfoService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/business/focus-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FocusInfoController {

    private final IFocusInfoService focusInfoService;

    /**
     * 分页查询关注人信息
     *
     * @param page
     * @param focusInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, FocusInfo focusInfo) {
        return R.ok(focusInfoService.focusInfoByPage(page, focusInfo));
    }

    /**
     * 分页查询关注人信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/list/{userId}")
    public R list(@PathVariable("userId") Integer userId) {
        return R.ok(focusInfoService.focusInfoByUser(userId));
    }

    /**
     * 分页查询粉丝信息
     *
     * @param userId
     * @return
     */
    @GetMapping("/fans/list/{userId}")
    public R focusFansInfoByUser(@PathVariable("userId") Integer userId) {
        return R.ok(focusInfoService.focusFansInfoByUser(userId));
    }

    /**
     * 添加关注信息
     *
     * @param focusInfo
     * @return
     */
    @PostMapping
    public R save(FocusInfo focusInfo) {
        Integer count = focusInfoService.count(Wrappers.<FocusInfo>lambdaQuery().eq(FocusInfo::getCollectUserId, focusInfo.getCollectUserId())
                .eq(FocusInfo::getUserId, focusInfo.getUserId()));
        if (count > 0) {
            Boolean result = focusInfoService.update(Wrappers.<FocusInfo>lambdaUpdate().set(FocusInfo::getDeleteFlag, focusInfo.getDeleteFlag())
                    .eq(FocusInfo::getUserId, focusInfo.getUserId()).eq(FocusInfo::getCollectUserId, focusInfo.getCollectUserId()));
            return R.ok(result);
        } else {
            focusInfo.setCreateDate(DateUtil.formatDateTime(new Date()));
            return R.ok(focusInfoService.save(focusInfo));
        }
    }

    /**
     * 删除关注
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(focusInfoService.update(Wrappers.<FocusInfo>lambdaUpdate().set(FocusInfo::getDeleteFlag, 1).in(FocusInfo::getId, ids)));
    }

}
