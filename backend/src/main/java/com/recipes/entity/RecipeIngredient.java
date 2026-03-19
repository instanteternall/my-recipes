package com.recipes.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recipe_ingredient")
public class RecipeIngredient {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recipeId;
    private String ingredientName;
    private java.math.BigDecimal quantity;
    private String unit;
}
