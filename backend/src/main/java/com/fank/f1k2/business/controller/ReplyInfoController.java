package com.fank.f1k2.business.controller;


import com.fank.f1k2.common.utils.R;
import com.fank.f1k2.business.entity.*;
import com.fank.f1k2.business.service.*;
import com.fank.f1k2.system.domain.User;
import com.fank.f1k2.system.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author FanK
 */
@RestController
@RequestMapping("/business/reply-info")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReplyInfoController {

    private final IReplyInfoService replyInfoService;

    private final IFocusInfoService focusInfoService;

    private final ICollectInfoService collectInfoService;

    private final INotifyInfoService notifyInfoService;

    private final UserService userService;

    private final IPostInfoService postInfoService;

    private final IUserInfoService userInfoService;

    /**
     * 分页查询回复信息
     *
     * @param page
     * @param replyInfo
     * @return
     */
    @GetMapping("/page")
    public R page(Page page, ReplyInfo replyInfo) {
        return R.ok(replyInfoService.replyInfoByPage(page, replyInfo));
    }

    /**
     * 获取具体的帖子回复信息
     *
     * @param postId
     * @return
     */
    @GetMapping("/list/{id}")
    public R replyListByPostId(@PathVariable(value = "id") Integer postId) {
        PostInfo postInfo = postInfoService.getById(postId);
        postInfoService.update(Wrappers.<PostInfo>lambdaUpdate().set(PostInfo::getPageviews, postInfo.getPageviews() + 1).eq(PostInfo::getId, postId));
        return R.ok(replyInfoService.replyListByPostId(postId));
    }

    /**
     * 添加回复信息
     *
     * @param replyInfo
     * @return
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R save(ReplyInfo replyInfo) {
        String check = this.contentCheck(replyInfo.getContent());
        if (StrUtil.isNotEmpty(check)) {
            return R.error(500, check);
        }
        {
            // 获取贴子信息
            PostInfo postInfo = postInfoService.getById(replyInfo.getPostId());
            // 获取回复人信息
            UserInfo user = userInfoService.getOne(Wrappers.<UserInfo>lambdaQuery().eq(UserInfo::getUserId, replyInfo.getUserId()));
            String username = user != null ? user.getName() : "用户";
            List<NotifyInfo> messageInfoList = new ArrayList<>();
            List<CollectInfo> collectInfoList = collectInfoService.list(Wrappers.<CollectInfo>lambdaQuery().eq(CollectInfo::getPostId, replyInfo.getPostId()).eq(CollectInfo::getDeleteFlag, 0));
            String message = "您收藏的贴子《" + postInfo.getTitle() + "》，" + username + "进行了回复，快去看看吧";
            for (CollectInfo collectInfo : collectInfoList) {
                messageInfoList.add(new NotifyInfo(Math.toIntExact(collectInfo.getUserId()), message, DateUtil.formatDateTime(new Date())));
            }

            String message1 = "您的贴子《" + postInfo.getTitle() + "》，" + username + "进行了回复，快去看看吧";
            NotifyInfo messageInfo = new NotifyInfo(Math.toIntExact(postInfo.getUserId()), message1, DateUtil.formatDateTime(new Date()));

            messageInfoList.add(messageInfo);
            notifyInfoService.saveBatch(messageInfoList);
            replyInfo.setDeleteFlag(0);
            replyInfo.setSendCreate(DateUtil.formatDateTime(new Date()));
            return R.ok(replyInfoService.save(replyInfo));
        }
    }

    /**
     * 删除回复信息
     *
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public R deleteByIds(@PathVariable("ids") List<Integer> ids) {
        return R.ok(replyInfoService.update(Wrappers.<ReplyInfo>lambdaUpdate().set(ReplyInfo::getDeleteFlag, 1)
                .in(ReplyInfo::getId, ids)));
    }

    /**
     * 规范检查
     *
     * @param content
     */
    public String contentCheck(String content) {
        String result = "";
        if (SensitiveWordHelper.contains(content)) {
            return SensitiveWordHelper.findFirst(content) + " ， 使用不规范，请更改";
        }
        return result;
    }

}
