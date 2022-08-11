/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/21
 * Time: 18:12
 * Describe: 分类
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shentu.reggie.common.R;
import com.shentu.reggie.entity.Category;
import com.shentu.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @return R
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("获取 category: {}", category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public R<Page<Category>> page(int page, int pageSize) {
        log.info("分页查询菜品信息，page: {}, pageSize: {}", page, pageSize);

        // 构造分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        // 构造条件构造器
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(Category::getSort);
        // 查询
        categoryService.page(pageInfo, qw);
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        log.info("删除分类，id: {}", ids);
        categoryService.remove(ids);
        return R.success("删除分类成功");
    }

    /**
     * 修改分类信息
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("更新分类，category: {}", category);
        categoryService.updateById(category);
        return R.success("更新分类成功");
    }

    /**
     * 获取分类数据
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        // 条件构造器
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        // 添加条件
        qw.eq(category.getType() != null, Category::getType, category.getType());
        qw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(qw);
        return R.success(list);
    }

}
