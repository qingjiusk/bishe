package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("orders")
public class Orders {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long buyerId;
    
    private Long sellerId;
    
    private BigDecimal totalAmount;
    
    private Integer deliveryType;
    
    private BigDecimal deliveryFee;
    
    private Integer status;
    
    private String address;
    
    private String paymentMethod;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private LocalDateTime shipTime;
    
    private LocalDateTime receiveTime;
}