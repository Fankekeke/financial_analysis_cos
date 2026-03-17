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
 * 问题回答表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PartyAnswers implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 关联的问题ID
     */
    private Integer questionId;

    /**
     * 回答者ID
     */
    private Integer userId;

    /**
     * 回答内容
     */
    private String content;

    /**
     * 回答时间
     */
    private String createdAt;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String title;


}
