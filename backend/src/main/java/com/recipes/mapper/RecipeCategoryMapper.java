package com.recipes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipes.entity.RecipeCategory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeCategoryMapper extends BaseMapper<RecipeCategory> {
}
