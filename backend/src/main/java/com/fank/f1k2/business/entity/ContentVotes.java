package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 顶踩投票记录表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ContentVotes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 投票用户ID
     */
    private Integer userId;

    /**
     * 回答ID
     */
    private Integer answerId;

    /**
     * 投票类型：1-顶(赞同), 0-踩(反对)
     */
    private Integer voteType;

    /**
     * 投票时间
     */
    private String createdAt;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String title;


}
