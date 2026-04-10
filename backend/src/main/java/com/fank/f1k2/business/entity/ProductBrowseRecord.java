package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 理财产品浏览记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProductBrowseRecord implements Serializable {

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
     * 浏览时间
     */
    private String browseTime;

    /**
     * 浏览时长(秒)
     */
    private Integer browseDuration;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    private String createdAt;


}
