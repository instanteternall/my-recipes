package com.recipes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.recipes.entity.RecipeTagRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecipeTagRelationMapper extends BaseMapper<RecipeTagRelation> {
    default void deleteByRecipeId(Long recipeId) {
        delete(new QueryWrapper<RecipeTagRelation>().eq("recipe_id", recipeId));
    }
}
