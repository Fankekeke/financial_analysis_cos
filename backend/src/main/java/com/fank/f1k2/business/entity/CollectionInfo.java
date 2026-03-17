package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * 题目表
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class CollectionInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 题目编号
     */
    private String code;

    /**
     * 关联的题库ID
     */
    private Integer collectionId;

    /**
     * 题目类型：填空题/单选题/多选题
     */
    private String questionType;

    /**
     * 分值
     */
    private Integer score;

    /**
     * 题目内容/题干
     */
    @TableField("`desc`")
    private String desc;

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 最后编辑时间
     */
    private String editTime;

    /**
     * 所属题库
     */
    private String tags;

    /**
     * 题目选项集合
     */
    @TableField(exist = false)
    private String collectionItem;


    @TableField(exist = false)
    private List<CollectionOptions> collectionOptionsList;
}
