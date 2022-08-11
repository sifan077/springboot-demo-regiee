/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/22
 * Time: 16:56
 * Describe:
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shentu.reggie.common.R;
import com.shentu.reggie.dto.SetmealDto;
import com.shentu.reggie.entity.Category;
import com.shentu.reggie.entity.Setmeal;
import com.shentu.reggie.service.CategoryService;
import com.shentu.reggie.service.DishService;
import com.shentu.reggie.service.SetmealDishService;
import com.shentu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息setmealDto : {}", setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(int page, int pageSize, String name) {
        // 分页构造器
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        // 添加条件
        qw.like(name != null, Setmeal::getName, name);
        qw.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(pageInfo, qw);

        // 对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");

        List<SetmealDto> list = new ArrayList<>();

        List<Setmeal> records = pageInfo.getRecords();
        for (Setmeal record : records) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(record, setmealDto);

            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            list.add(setmealDto);
        }
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }


    /**
     * 删除套餐
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("删除的列表{}", ids);
        setmealService.removeWithDish(ids);
        return R.success("删除成功");
    }

    /**
     * 更新套餐是否停售，不完善，未跟套餐进行结合
     */
    @PostMapping("/status/{i}")
    public R<String> status(@RequestParam List<Long> ids, @PathVariable Integer i) {
        // 停售套餐

        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId, ids);
        List<Setmeal> setmeals = setmealService.list(qw);
        for (Setmeal setmeal : setmeals) {
            setmeal.setStatus(i);
        }
        setmealService.updateBatchById(setmeals);


        return R.success("修改状态成功");
    }

    /**
     * 获取单个套餐详情
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        if (id == null) return R.error("出现错误");
        SetmealDto setmealDto = setmealService.getWithDish(id);

        return R.success(setmealDto);
    }

    /**
     * 更新套餐
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateWithDish(setmealDto);
        return R.success("成功修改");
    }

    /**
     * 根据条件查询套餐
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        qw.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        qw.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmeals = setmealService.list(qw);
        return R.success(setmeals);
    }
}
