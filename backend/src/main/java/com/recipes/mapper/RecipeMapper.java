package com.recipes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipes.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
    List<Recipe> selectByUserId(@Param("userId") Long userId);
    List<Recipe> selectByKeyword(@Param("keyword") String keyword);
}
