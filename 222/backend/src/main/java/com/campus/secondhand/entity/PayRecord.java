package com.campus.secondhand.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pay_record")
public class PayRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String payNo;

    private Long buyerId;

    private BigDecimal totalAmount;

    private Integer payStatus;

    private LocalDateTime payTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
