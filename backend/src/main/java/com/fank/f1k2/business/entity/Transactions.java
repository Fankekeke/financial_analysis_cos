package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 交易记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Transactions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * 关联账户
     */
    private Long accountId;

    /**
     * 关联分类
     */
    private Long categoryId;

    /**
     * 金额
     */
    private BigDecimal amount;

    private String transactionType;

    /**
     * 记账/发生时间
     */
    private LocalDateTime transactionTime;

    private String remark;

    private LocalDateTime createdAt;


}
