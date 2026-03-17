package com.fank.f1k2.business.service.impl;

import com.fank.f1k2.business.dao.NotifyInfoMapper;
import com.fank.f1k2.business.entity.NotifyInfo;
import com.fank.f1k2.business.service.INotifyInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author FanK fan1ke2ke@gmail.com
 */
@Service
public class NotifyInfoServiceImpl extends ServiceImpl<NotifyInfoMapper, NotifyInfo> implements INotifyInfoService {

    /**
     * 分页获取消息通知信息
     *
     * @param page       分页对象
     * @param notifyInfo 消息通知信息
     * @return 结果
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryNotifyPage(Page<NotifyInfo> page, NotifyInfo notifyInfo) {
        return baseMapper.queryNotifyPage(page, notifyInfo);
    }
    /**
     * 获取用户消息通知信息列表
     *
     * @param page       分页对象
     * @param notifyInfo 搜索条件
     * @return 列表
     */
    @Override
    public IPage<LinkedHashMap<String, Object>> queryNotifyByUser(Page<NotifyInfo> page, NotifyInfo notifyInfo) {
        return baseMapper.queryNotifyByUser(page, notifyInfo);
    }

    @Override
    public IPage<LinkedHashMap<String, Object>> queryNotifyByStaff(Page<NotifyInfo> page, NotifyInfo notifyInfo) {
        return baseMapper.queryNotifyByStaff(page, notifyInfo);
    }

    /**
     * 根据用户ID获取消息通知信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public List<LinkedHashMap<String, Object>> queryNotifyByUser(Integer userId) {
        return baseMapper.queryNotifyByUserId(userId);
    }
}
