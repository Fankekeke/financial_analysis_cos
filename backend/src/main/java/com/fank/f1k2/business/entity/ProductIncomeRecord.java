package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 理财产品历史收益记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProductIncomeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 理财产品ID
     */
    private Integer productId;

    /**
     * 理财产品名称
     */
    private String productName;

    /**
     * 理财产品类型
     */
    private String productType;

    /**
     * 投资金额
     */
    private BigDecimal investmentAmount;

    /**
     * 收益金额
     */
    private BigDecimal incomeAmount;

    /**
     * 收益率(%)
     */
    private BigDecimal incomeRate;

    /**
     * 收益日期
     */
    private String incomeDate;

    /**
     * 收益类型：分红/利息/增值等
     */
    private String incomeType;

    /**
     * 状态：1-已到账，0-未到账
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 更新时间
     */
    private String updatedAt;


}
