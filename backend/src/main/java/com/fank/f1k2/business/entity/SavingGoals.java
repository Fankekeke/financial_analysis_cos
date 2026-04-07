package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 存钱计划
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SavingGoals implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * 旅游基金、首付等
     */
    private String goalName;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private String startDate;

    private String endDate;

    /**
     * 系统测算的每月需存金额
     */
    private BigDecimal monthlySuggestion;

    private String status;

    @TableField(exist = false)
    private String userName;
}
