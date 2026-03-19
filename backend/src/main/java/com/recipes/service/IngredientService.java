package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.*;
import com.recipes.mapper.*;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

@Service
public class IngredientService {
    private final IngredientMapper ingredientMapper;
    private final IngredientCategoryMapper categoryMapper;

    public IngredientService(IngredientMapper ingredientMapper, IngredientCategoryMapper categoryMapper) {
        this.ingredientMapper = ingredientMapper;
        this.categoryMapper = categoryMapper;
    }

    public List<Ingredient> getIngredientList(Long userId, Integer page, Integer pageSize, Long categoryId, String keyword) {
        Page<Ingredient> p = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        QueryWrapper<Ingredient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        wrapper.orderByDesc("created_at");
        Page<Ingredient> result = ingredientMapper.selectPage(p, wrapper);
        
        for (Ingredient ingredient : result.getRecords()) {
            loadIngredientRelations(ingredient);
        }
        return result.getRecords();
    }

    public Long getTotal(Long userId, Long categoryId, String keyword) {
        QueryWrapper<Ingredient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        return ingredientMapper.selectCount(wrapper);
    }

    public Ingredient getIngredientById(Long id) {
        Ingredient ingredient = ingredientMapper.selectById(id);
        if (ingredient != null) {
            loadIngredientRelations(ingredient);
        }
        return ingredient;
    }

    public Ingredient createIngredient(Long userId, CreateIngredientRequest request) {
        Ingredient ingredient = new Ingredient();
        ingredient.setUserId(userId);
        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setExpiryDate(request.getExpiryDate());
        ingredient.setCategoryId(request.getCategoryId());
        ingredientMapper.insert(ingredient);
        return getIngredientById(ingredient.getId());
    }

    public Ingredient updateIngredient(Long id, Long userId, CreateIngredientRequest request) {
        Ingredient ingredient = ingredientMapper.selectById(id);
        if (ingredient == null) {
            throw new RuntimeException("食材不存在");
        }
        if (!ingredient.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }

        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setExpiryDate(request.getExpiryDate());
        ingredient.setCategoryId(request.getCategoryId());
        ingredientMapper.updateById(ingredient);
        return getIngredientById(id);
    }

    public void deleteIngredient(Long id, Long userId) {
        Ingredient ingredient = ingredientMapper.selectById(id);
        if (ingredient == null) {
            throw new RuntimeException("食材不存在");
        }
        if (!ingredient.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }
        ingredientMapper.deleteById(id);
    }

    public List<IngredientCategory> getCategories() {
        return categoryMapper.selectList(null);
    }

    public List<Ingredient> searchIngredients(String keyword) {
        return ingredientMapper.selectByKeyword(keyword);
    }

    private void loadIngredientRelations(Ingredient ingredient) {
        if (ingredient.getCategoryId() != null) {
            IngredientCategory category = categoryMapper.selectById(ingredient.getCategoryId());
            if (category != null) {
                ingredient.setCategoryName(category.getName());
            }
        }
    }
}
