package com.recipes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.recipes.entity.Ingredient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface IngredientMapper extends BaseMapper<Ingredient> {
    List<Ingredient> selectByUserId(@Param("userId") Long userId);
    List<Ingredient> selectByKeyword(@Param("keyword") String keyword);
}
