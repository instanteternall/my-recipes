package com.recipes.service;

import com.recipes.dto.*;
import com.recipes.entity.*;
import com.recipes.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

@Service
public class RecipeService {
    private final RecipeMapper recipeMapper;
    private final RecipeCategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final RecipeIngredientMapper ingredientMapper;
    private final CookingStepMapper stepMapper;
    private final RecipeTagRelationMapper tagRelationMapper;

    public RecipeService(RecipeMapper recipeMapper, RecipeCategoryMapper categoryMapper,
                         TagMapper tagMapper, RecipeIngredientMapper ingredientMapper,
                         CookingStepMapper stepMapper, RecipeTagRelationMapper tagRelationMapper) {
        this.recipeMapper = recipeMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.ingredientMapper = ingredientMapper;
        this.stepMapper = stepMapper;
        this.tagRelationMapper = tagRelationMapper;
    }

    public List<Recipe> getRecipeList(Long userId, Integer page, Integer pageSize, Long categoryId, Long tagId, String keyword) {
        Page<Recipe> p = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        wrapper.orderByDesc("created_at");
        Page<Recipe> result = recipeMapper.selectPage(p, wrapper);
        for (Recipe recipe : result.getRecords()) {
            loadRecipeRelations(recipe);
        }
        return result.getRecords();
    }

    public Long getTotal(Long userId, Long categoryId, String keyword) {
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword);
        }
        return recipeMapper.selectCount(wrapper);
    }

    public Recipe getRecipeById(Long id) {
        Recipe recipe = recipeMapper.selectById(id);
        if (recipe != null) {
            loadRecipeRelations(recipe);
        }
        return recipe;
    }

    @Transactional
    public Recipe createRecipe(Long userId, CreateRecipeRequest request) {
        Recipe recipe = new Recipe();
        recipe.setUserId(userId);
        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setCoverImage(request.getCoverImage());
        recipe.setCookingTime(request.getCookingTime());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setCategoryId(request.getCategoryId());
        recipeMapper.insert(recipe);

        saveIngredients(recipe.getId(), request.getIngredients());
        saveSteps(recipe.getId(), request.getSteps());
        saveTagRelations(recipe.getId(), request.getTagIds());

        return getRecipeById(recipe.getId());
    }

    @Transactional
    public Recipe updateRecipe(Long id, Long userId, CreateRecipeRequest request) {
        Recipe recipe = recipeMapper.selectById(id);
        if (recipe == null) {
            throw new RuntimeException("菜谱不存在");
        }
        if (!recipe.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改");
        }

        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setCoverImage(request.getCoverImage());
        recipe.setCookingTime(request.getCookingTime());
        recipe.setDifficulty(request.getDifficulty());
        recipe.setCategoryId(request.getCategoryId());
        recipeMapper.updateById(recipe);

        ingredientMapper.deleteByRecipeId(id);
        saveIngredients(id, request.getIngredients());

        stepMapper.deleteByRecipeId(id);
        saveSteps(id, request.getSteps());

        tagRelationMapper.deleteByRecipeId(id);
        saveTagRelations(id, request.getTagIds());

        return getRecipeById(id);
    }

    @Transactional
    public void deleteRecipe(Long id, Long userId) {
        Recipe recipe = recipeMapper.selectById(id);
        if (recipe == null) {
            throw new RuntimeException("菜谱不存在");
        }
        if (!recipe.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除");
        }
        ingredientMapper.deleteByRecipeId(id);
        stepMapper.deleteByRecipeId(id);
        tagRelationMapper.deleteByRecipeId(id);
        recipeMapper.deleteById(id);
    }

    public List<RecipeCategory> getCategories() {
        return categoryMapper.selectList(null);
    }

    public List<Tag> getTags() {
        return tagMapper.selectList(null);
    }

    public List<Recipe> searchRecipes(String keyword, Long categoryId, String difficulty) {
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("description", keyword));
        }
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        if (difficulty != null && !difficulty.isEmpty()) {
            wrapper.eq("difficulty", difficulty);
        }
        wrapper.orderByDesc("view_count", "created_at");
        wrapper.last("LIMIT 30");
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        for (Recipe recipe : recipes) {
            loadRecipeRelations(recipe);
        }
        return recipes;
    }

    public List<Recipe> getHotRecipes() {
        QueryWrapper<Recipe> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("view_count", "created_at");
        wrapper.last("LIMIT 10");
        List<Recipe> recipes = recipeMapper.selectList(wrapper);
        for (Recipe recipe : recipes) {
            loadRecipeRelations(recipe);
        }
        return recipes;
    }

    // ---- 私有辅助方法 ----

    private void loadRecipeRelations(Recipe recipe) {
        if (recipe.getCategoryId() != null) {
            RecipeCategory category = categoryMapper.selectById(recipe.getCategoryId());
            if (category != null) {
                recipe.setCategoryName(category.getName());
            }
        }
        recipe.setTags(tagMapper.selectByRecipeId(recipe.getId()));
        recipe.setIngredients(ingredientMapper.selectByRecipeId(recipe.getId()));
        recipe.setSteps(stepMapper.selectByRecipeId(recipe.getId()));
    }

    private void saveIngredients(Long recipeId, List<RecipeIngredientDTO> dtos) {
        if (dtos == null) return;
        for (RecipeIngredientDTO dto : dtos) {
            if (dto.getIngredientName() == null || dto.getIngredientName().trim().isEmpty()) continue;
            RecipeIngredient ingredient = new RecipeIngredient();
            ingredient.setRecipeId(recipeId);
            ingredient.setIngredientName(dto.getIngredientName());
            ingredient.setQuantity(dto.getQuantity());
            ingredient.setUnit(dto.getUnit());
            ingredientMapper.insert(ingredient);
        }
    }

    private void saveSteps(Long recipeId, List<CookingStepDTO> dtos) {
        if (dtos == null) return;
        for (int i = 0; i < dtos.size(); i++) {
            CookingStepDTO dto = dtos.get(i);
            if (dto.getContent() == null || dto.getContent().trim().isEmpty()) continue;
            CookingStep step = new CookingStep();
            step.setRecipeId(recipeId);
            step.setStepOrder(i + 1);
            step.setContent(dto.getContent());
            step.setImage(dto.getImage());
            stepMapper.insert(step);
        }
    }

    private void saveTagRelations(Long recipeId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) return;
        for (Long tagId : tagIds) {
            RecipeTagRelation relation = new RecipeTagRelation();
            relation.setRecipeId(recipeId);
            relation.setTagId(tagId);
            tagRelationMapper.insert(relation);
        }
    }
}
