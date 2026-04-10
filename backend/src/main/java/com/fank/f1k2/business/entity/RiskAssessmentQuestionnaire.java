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
 * 风险评估问卷主表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskAssessmentQuestionnaire implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 问卷编码
     */
    private String questionnaireCode;

    /**
     * 问卷名称
     */
    private String questionnaireName;

    /**
     * 问卷版本
     */
    private String questionnaireVersion;

    /**
     * 问卷描述
     */
    private String description;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 及格分数
     */
    private Integer passingScore;

    /**
     * 答题时限(分钟)
     */
    private Integer timeLimit;

    /**
     * 状态：1-启用，0-禁用
     */
    private String status;

    /**
     * 是否默认问卷：1-是，0-否
     */
    private String isDefault;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;

    @TableField(exist = false)
    private String optionStr;

}
