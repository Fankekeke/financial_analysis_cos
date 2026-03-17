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
 * 学习足迹与行为记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserLearningTrace implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 行为状态（'查询','提交','搜索'）
     */
    private String actionType;

    /**
     * 关联具体的题目ID或知识点ID
     */
    private Integer targetId;

    /**
     * 关键标签
     */
    private String keyWord;

    /**
     * 创建时间
     */
    private String createdAt;

    @TableField(exist = false)
    private String userName;


}
