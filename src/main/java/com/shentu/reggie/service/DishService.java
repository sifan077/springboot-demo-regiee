package com.shentu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shentu.reggie.dto.DishDto;
import com.shentu.reggie.entity.Dish;

/**
 * 菜品(Dish)表服务接口
 *
 * @author shentu
 * @since 2022-06-21 18:04:12
 */
public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要操作两张表，dish,dish_flavor
    public void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);
}
