package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 新增菜品和对应的口味
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        log.info("dish: {}", dishDTO);
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            flavors.forEach((item)->{
                item.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        // 1. 分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 1. 查询菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishId(ids);
        if(setMealIds!=null && !setMealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 2. 批量删除菜品
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 1. 查询菜品
        Dish dish = dishMapper.getById(id);
        if(dish != null) {
            // 2. 查询口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(flavors);
            return dishVO;
        }
        return null;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        // 1. 更新菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        log.info("更新成功");
        // 2. 删除口味
        dishFlavorMapper.deleteByDishId(dish.getId());
        // 3. 新增口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && !flavors.isEmpty()){
            flavors.forEach((item)->{
                item.setDishId(dish.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
