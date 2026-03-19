package com.recipes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cooking_step")
public class CookingStep {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recipeId;
    private Integer stepOrder;
    private String content;
    private String image;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
