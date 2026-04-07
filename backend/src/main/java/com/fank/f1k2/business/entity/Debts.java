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
 * 负债管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Debts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * 如：房贷、借张三款
     */
    private String debtName;

    private BigDecimal totalAmount;

    private BigDecimal remainingAmount;

    /**
     * 年利率
     */
    private BigDecimal interestRate;

    /**
     * 还款截止日
     */
    private String dueDate;

    /**
     * 单次或每月
     */
    private String debtType;

    private String createdAt;
    private String content;

    @TableField(exist = false)
    private String userName;

}
