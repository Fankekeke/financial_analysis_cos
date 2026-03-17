package com.fank.f1k2.business.entity;

import java.math.BigDecimal;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 存款账户信息
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Accounts implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * 名称如：招行卡1、支付宝
     */
    private String accountName;

    private String accountType;

    /**
     * 实时余额
     */
    private BigDecimal balance;

    private String remark;

    /**
     * 1-正常, 0-注销
     */
    private Integer status;


}
