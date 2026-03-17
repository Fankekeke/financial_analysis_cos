package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fank.f1k2.business.entity.vo.CollectionItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 题库信息
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class QuestionBank implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 题库编号
     */
    private String code;

    /**
     * 题库名称
     */
    private String name;

    /**
     * 图片
     */
    private String image;

    /**
     * 创建时间
     */
    private String createDate;

    /**
     * 题库选项集合
     */
//    @TableField(exist = false)
//    private List<CollectionItem> collectionItemList;

    @TableField(exist = false)
    private String collectionItemList;
}
