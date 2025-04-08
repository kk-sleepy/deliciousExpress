package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id查询套餐id
     *
     * @param dishIds 菜品id
     * @return 套餐id集合
     */
    List<Long> getSetMealIdsByDishId(List<Long> dishIds);
}
