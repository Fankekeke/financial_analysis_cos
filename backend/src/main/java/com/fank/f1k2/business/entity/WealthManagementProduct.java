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
 * 理财产品管理
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WealthManagementProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类型：基金/股票/债券/银行理财/保险等
     */
    private String productType;

    /**
     * 风险等级：R1/R2/R3/R4/R5
     */
    private String riskLevel;

    /**
     * 发行机构
     */
    private String issuer;

    /**
     * 预期年化收益率(%)
     */
    private BigDecimal expectedReturnRate;

    /**
     * 最低投资金额
     */
    private BigDecimal minInvestment;

    /**
     * 最高投资金额
     */
    private BigDecimal maxInvestment;

    /**
     * 投资期限(天)
     */
    private Integer investmentPeriod;

    /**
     * 起息日期
     */
    private String startDate;

    /**
     * 到期日期
     */
    private String endDate;

    /**
     * 产品状态：1-募集期，2-运作中，3-已到期，0-下架
     */
    private String productStatus;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 产品特点
     */
    private String features;

    /**
     * 购买规则
     */
    private String purchaseRule;

    /**
     * 赎回规则
     */
    private String redemptionRule;

    /**
     * 手续费率(%)
     */
    private BigDecimal feeRate;

    /**
     * 管理费率(%)
     */
    private BigDecimal managementFee;

    /**
     * 产品总规模
     */
    private BigDecimal totalScale;

    /**
     * 剩余可投规模
     */
    private BigDecimal remainingScale;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 详情图片(JSON数组)
     */
    private String detailImages;

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


}
