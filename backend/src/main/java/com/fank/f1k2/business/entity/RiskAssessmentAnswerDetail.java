package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 用户风险评估答题详情
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RiskAssessmentAnswerDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 答题记录ID
     */
    private Integer answerId;

    /**
     * 问题ID
     */
    private Integer questionId;

    /**
     * 问题内容
     */
    private String questionContent;

    /**
     * 选中的选项ID列表(逗号分隔)
     */
    private String selectedOptionIds;

    /**
     * 选中的选项内容
     */
    private String selectedOptionContents;

    /**
     * 本题得分
     */
    private Integer questionScore;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;


}
