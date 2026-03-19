package com.recipes.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecipeIngredientDTO {
    private Long id;
    private String ingredientName;
    private BigDecimal quantity;
    private String unit;
}
