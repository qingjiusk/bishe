package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long sellerId;
    
    private String name;
    
    @TableField(exist = false)
    private String sellerName;
    
    @TableField(exist = false)
    private String sellerPhone;
    
    private String description;
    
    private BigDecimal price;
    
    private Integer categoryId;
    
    @TableField("`condition`")
    private String condition;
    
    private String images;
    
    private String thumbnail;
    
    private String location;
    
    private Integer status;
    
    private Integer auditStatus;
    
    private String auditReason;
    
    private Integer viewCount;
    
    private Integer unreadComment;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}