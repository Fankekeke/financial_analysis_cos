package com.fank.f1k2.business.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * AI答疑
 *
 * @author FanK fan1ke2ke@gmail.com（悲伤的橘子树）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UserQuestions implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键ID
    */
    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * 所属用户
     */
    private Integer userId;

    /**
     * 问题内容
     */
    private String content;

    /**
     * AI自动生成的回答
     */
    private String aiAnswer;

    /**
     * 管理者回复
     */
    private String adminReply;

    /**
     * 关联关键词
     */
    private String keyWord;

    /**
     * 状态（'进行中','已回答','已采纳'）
     */
    private String status;

    /**
     * 创建时间
     */
    private String createdAt;

    @TableField(exist = false)
    private String userName;

    /**
     * 获取标题或内容摘要
     * @return 标题或内容前 50 字
     */
    public String getTitleOrContent() {
        if (this.content != null && this.content.length() > 0) {
            return this.content.length() > 50 ? this.content.substring(0, 50) + "..." : this.content;
        }
        return "";
    }
}
