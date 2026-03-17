package com.fank.f1k2.business.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 分类与标签
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Categories implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    private Long userId;

    /**
     * 分类名：餐饮、交通等
     */
    private String name;

    private String type;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 支持二级分类
     */
    private Long parentId;


}
