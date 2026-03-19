package com.recipes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("recipe")
public class Recipe {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String coverImage;
    private Integer cookingTime;
    private String difficulty;
    private Long categoryId;
    @TableField(exist = false)
    private String categoryName;
    @TableField(exist = false)
    private List<Tag> tags;
    @TableField(exist = false)
    private List<RecipeIngredient> ingredients;
    @TableField(exist = false)
    private List<CookingStep> steps;
    private Integer viewCount;
    @TableLogic
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
