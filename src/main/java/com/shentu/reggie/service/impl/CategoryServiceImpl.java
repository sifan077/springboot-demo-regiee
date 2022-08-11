package com.shentu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shentu.reggie.common.CustomException;
import com.shentu.reggie.entity.Category;
import com.shentu.reggie.entity.Dish;
import com.shentu.reggie.entity.Setmeal;
import com.shentu.reggie.mapper.CategoryMapper;
import com.shentu.reggie.service.CategoryService;
import com.shentu.reggie.service.DishService;
import com.shentu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前进行判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 查询当前分类是否关联菜品，如果关联则抛出异常
        LambdaQueryWrapper<Dish> dqw = new LambdaQueryWrapper<>();
        // 添加查询条件
        dqw.eq(Dish::getCategoryId, id);
        // 查询
        int count = dishService.count(dqw);
        if (count > 0) {
            // 关联菜品，抛出异常
            throw new CustomException("当前分类已关联菜品，不能删除");

        }
        // 查询当前分类是否关联套餐，如果关联则抛出异常
        LambdaQueryWrapper<Setmeal> sqw = new LambdaQueryWrapper<>();
        // 添加查询条件
        sqw.eq(Setmeal::getCategoryId, id);
        // 查询
        count = setmealService.count(sqw);
        if (count > 0) {
            // 关联套餐，抛出异常
            throw new CustomException("当前分类已关联套餐，不能删除");
        }
        // 正常删除
        super.removeById(id);
    }
}
