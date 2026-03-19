package com.recipes.controller;

import com.recipes.dto.*;
import com.recipes.entity.Recipe;
import com.recipes.entity.RecipeCategory;
import com.recipes.entity.Tag;
import com.recipes.service.RecipeService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes")
    public Result<Map<String, Object>> getRecipeList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword) {
        
        Long userId = getCurrentUserId();
        List<Recipe> list = recipeService.getRecipeList(userId, page, pageSize, categoryId, tagId, keyword);
        Long total = recipeService.getTotal(userId, categoryId, keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    @GetMapping("/recipes/{id}")
    public Result<Recipe> getRecipeById(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return Result.error(404, "菜谱不存在");
        }
        return Result.success(recipe);
    }

    @PostMapping("/recipes")
    public Result<Recipe> createRecipe(@RequestBody CreateRecipeRequest request) {
        Long userId = getCurrentUserId();
        try {
            Recipe recipe = recipeService.createRecipe(userId, request);
            return Result.success(recipe);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/recipes/{id}")
    public Result<Recipe> updateRecipe(@PathVariable Long id, @RequestBody CreateRecipeRequest request) {
        Long userId = getCurrentUserId();
        try {
            Recipe recipe = recipeService.updateRecipe(id, userId, request);
            return Result.success(recipe);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/recipes/{id}")
    public Result<Void> deleteRecipe(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        try {
            recipeService.deleteRecipe(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/recipes/categories")
    public Result<List<RecipeCategory>> getCategories() {
        List<RecipeCategory> categories = recipeService.getCategories();
        return Result.success(categories);
    }

    @GetMapping("/recipes/tags")
    public Result<List<Tag>> getTags() {
        List<Tag> tags = recipeService.getTags();
        return Result.success(tags);
    }

    @GetMapping("/recipes/hot")
    public Result<List<Recipe>> getHotRecipes() {
        List<Recipe> recipes = recipeService.getHotRecipes();
        return Result.success(recipes);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
