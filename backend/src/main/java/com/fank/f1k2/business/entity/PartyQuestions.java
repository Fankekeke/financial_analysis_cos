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
 * 党员社区问题表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PartyQuestions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 提问者ID
     */
    private Integer userId;

    /**
     * 问题标题
     */
    private String title;

    /**
     * 问题详细内容
     */
    private String content;

    /**
     * 问题状态（进行中 已关闭 已采纳）
     */
    private String status;

    /**
     * 提问时间
     */
    private String createdAt;

    @TableField(exist = false)
    private String userName;


}
