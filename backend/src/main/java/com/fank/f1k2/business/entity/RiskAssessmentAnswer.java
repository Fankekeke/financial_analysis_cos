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
 * 用户风险评估答题记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskAssessmentAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 问卷ID
     */
    private Integer questionnaireId;

    /**
     * 问卷名称
     */
    private String questionnaireName;

    /**
     * 总得分
     */
    private Integer totalScore;

    /**
     * 风险等级：保守型/稳健型/平衡型/成长型/进取型
     */
    private String riskLevel;

    /**
     * 风险等级编码：C1/C2/C3/C4/C5
     */
    private String riskLevelCode;

    /**
     * 答题时间
     */
    private String answerTime;

    /**
     * 答题时长(秒)
     */
    private Integer duration;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 状态：1-已完成，0-未完成
     */
    private String status;

    /**
     * 创建时间
     */
    private String createdAt;

    @TableField(exist = false)
    private String detailStr;


}
