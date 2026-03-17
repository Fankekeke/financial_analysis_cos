package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 固定资产
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FixedAssets implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * 房、车、手机等
     */
    private String assetName;

    private String assetCategory;

    /**
     * 购买价格
     */
    private BigDecimal purchasePrice;

    /**
     * 购买时间
     */
    private LocalDate purchaseDate;

    /**
     * 当前估值（如黄金、房产会波动）
     */
    private BigDecimal currentValue;

    /**
     * 折旧率（可选）
     */
    private BigDecimal depreciationRate;

    private String remark;


}
