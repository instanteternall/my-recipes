package com.recipes.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateRecipeRequest {
    private String name;
    private String description;
    private String coverImage;
    private Integer cookingTime;
    private String difficulty;
    private Long categoryId;
    private List<Long> tagIds;
    private List<RecipeIngredientDTO> ingredients;
    private List<CookingStepDTO> steps;
}
