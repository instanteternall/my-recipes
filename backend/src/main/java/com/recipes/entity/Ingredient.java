package com.recipes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ingredient")
public class Ingredient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private java.math.BigDecimal quantity;
    private String unit;
    private LocalDate expiryDate;
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
