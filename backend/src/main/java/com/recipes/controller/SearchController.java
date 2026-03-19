package com.recipes.controller;

import com.recipes.dto.*;
import com.recipes.entity.Ingredient;
import com.recipes.entity.Recipe;
import com.recipes.service.IngredientService;
import com.recipes.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {
    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    public SearchController(RecipeService recipeService, IngredientService ingredientService) {
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
    }

    /**
     * 搜索菜谱：支持按关键词、分类、难度综合搜索
     */
    @GetMapping("/search/recipes")
    public Result<List<Recipe>> searchRecipes(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String difficulty) {
        List<Recipe> recipes = recipeService.searchRecipes(keyword, categoryId, difficulty);
        return Result.success(recipes);
    }

    /**
     * 搜索食材
     */
    @GetMapping("/search/ingredients")
    public Result<List<Ingredient>> searchIngredients(@RequestParam String keyword) {
        List<Ingredient> ingredients = ingredientService.searchIngredients(keyword);
        return Result.success(ingredients);
    }

    /**
     * 推荐菜谱（复用热门菜谱逻辑）
     */
    @GetMapping("/recommend/recipes")
    public Result<List<Recipe>> getRecommendRecipes() {
        List<Recipe> recipes = recipeService.getHotRecipes();
        return Result.success(recipes);
    }
}
