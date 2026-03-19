package com.recipes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipes.entity.CookingStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CookingStepMapper extends BaseMapper<CookingStep> {
    List<CookingStep> selectByRecipeId(@Param("recipeId") Long recipeId);
    void deleteByRecipeId(@Param("recipeId") Long recipeId);
}
