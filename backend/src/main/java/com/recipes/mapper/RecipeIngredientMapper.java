package com.recipes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipes.entity.RecipeIngredient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RecipeIngredientMapper extends BaseMapper<RecipeIngredient> {
    List<RecipeIngredient> selectByRecipeId(@Param("recipeId") Long recipeId);
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}
