package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 预算管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Budgets implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * NULL表示总预算
     */
    private Long categoryId;

    /**
     * 预算限额
     */
    private BigDecimal amountLimit;

    /**
     * 预算周期，如 2024-03
     */
    private String period;

    /**
     * 预警阈值，如0.8
     */
    private BigDecimal alertThreshold;

    /**
     * 是否已触发80%预警
     */
    private Integer isAlerted80;

    /**
     * 是否已触发100%预警
     */
    private Integer isAlerted100;

    @TableField(exist = false)
    private String userName;

}
