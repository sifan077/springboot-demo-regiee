/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/22
 * Time: 9:13
 * Describe: 菜品管理
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shentu.reggie.common.R;
import com.shentu.reggie.dto.DishDto;
import com.shentu.reggie.entity.Category;
import com.shentu.reggie.entity.Dish;
import com.shentu.reggie.entity.DishFlavor;
import com.shentu.reggie.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("DishDto:{}", dishDto);
        dishService.save(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     *
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page, int pageSize, String name) {
        // 构造分页构造器
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();

        // 添加过滤条件
        qw.like(name != null, Dish::getName, name);
        // 添加排序条件
        qw.orderByDesc(Dish::getUpdateTime);

        // 执行分页查询
        dishService.page(pageInfo, qw);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = new ArrayList<>();
        for (Dish record : records) {
            DishDto dto = new DishDto();
            BeanUtils.copyProperties(record, dto);

            Long categoryId = record.getCategoryId();
            // 根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();

            dto.setCategoryName(categoryName);
            list.add(dto);
        }

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        log.info("DishDto:{}", dishDto);
        return R.success(dishDto);
    }

    /**
     * 更新菜品
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("DishDto:{}", dishDto);
        dishService.updateWithFlavor(dishDto);
        return R.success("更新菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish) {
//        // 构造插叙条件
//        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
//        qw.eq(dish.getCategoryId() != null,
//                Dish::getCategoryId, dish.getCategoryId());
//        // 查询状态，1为正在出售
//        qw.eq(Dish::getStatus, 1);
//        // 添加排序条件
//        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishes = dishService.list(qw);
//
//        return R.success(dishes);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        // 构造插叙条件
        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.eq(dish.getCategoryId() != null,
                Dish::getCategoryId, dish.getCategoryId());
        // 查询状态，1为正在出售
        qw.eq(Dish::getStatus, 1);
        // 添加排序条件
        qw.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishes = dishService.list(qw);
        List<DishDto> dishDtos = new ArrayList<>();
        for (Dish item : dishes) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dfqw = new LambdaQueryWrapper<>();
            dfqw.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> flavors = dishFlavorService.list(dfqw);
            dishDto.setFlavors(flavors);
            dishDtos.add(dishDto);
        }
        return R.success(dishDtos);
    }

    /**
     * 更新菜品是否停售，不完善，未跟套餐进行结合
     */
    @PostMapping("/status/{i}")
    public R<String> status(@RequestParam List<Long> ids, @PathVariable Integer i) {

        LambdaQueryWrapper<Dish> qw = new LambdaQueryWrapper<>();
        qw.in(Dish::getId, ids);
        List<Dish> dishes = dishService.list(qw);
        for (Dish dish : dishes) {
            dish.setStatus(i);
            System.out.println(dish);
        }
        dishService.updateBatchById(dishes);


        return R.success("修改状态成功");
    }
}
