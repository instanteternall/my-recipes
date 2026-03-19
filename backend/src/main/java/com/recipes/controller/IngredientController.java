package com.recipes.controller;

import com.recipes.dto.*;
import com.recipes.entity.Ingredient;
import com.recipes.entity.IngredientCategory;
import com.recipes.service.IngredientService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class IngredientController {
    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @GetMapping("/ingredients")
    public Result<Map<String, Object>> getIngredientList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        Long userId = getCurrentUserId();
        List<Ingredient> list = ingredientService.getIngredientList(userId, page, pageSize, categoryId, keyword);
        Long total = ingredientService.getTotal(userId, categoryId, keyword);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    @GetMapping("/ingredients/{id}")
    public Result<Ingredient> getIngredientById(@PathVariable Long id) {
        Ingredient ingredient = ingredientService.getIngredientById(id);
        if (ingredient == null) {
            return Result.error(404, "食材不存在");
        }
        return Result.success(ingredient);
    }

    @PostMapping("/ingredients")
    public Result<Ingredient> createIngredient(@RequestBody CreateIngredientRequest request) {
        Long userId = getCurrentUserId();
        try {
            Ingredient ingredient = ingredientService.createIngredient(userId, request);
            return Result.success(ingredient);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PutMapping("/ingredients/{id}")
    public Result<Ingredient> updateIngredient(@PathVariable Long id, @RequestBody CreateIngredientRequest request) {
        Long userId = getCurrentUserId();
        try {
            Ingredient ingredient = ingredientService.updateIngredient(id, userId, request);
            return Result.success(ingredient);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @DeleteMapping("/ingredients/{id}")
    public Result<Void> deleteIngredient(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        try {
            ingredientService.deleteIngredient(id, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/ingredients/categories")
    public Result<List<IngredientCategory>> getCategories() {
        List<IngredientCategory> categories = ingredientService.getCategories();
        return Result.success(categories);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }
}
