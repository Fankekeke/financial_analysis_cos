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
 * 答题记录
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AnswerRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 答题用户
     */
    private Integer userId;

    /**
     * 所属题库
     */
    private Integer bankId;

    /**
     * 总分
     */
    private Integer totalScore;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 答题详情
     */
    private String answerDetail;

    /**
     * 答题时间
     */
    private String createDate;

    @TableField(exist = false)
    private List<CollectionItem> collectionItemList;
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String bankName;


}
