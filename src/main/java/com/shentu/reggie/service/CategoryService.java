package com.shentu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shentu.reggie.entity.Category;

/**
 * 分类(Category)表服务接口
 *
 * @author shentu
 * @since 2022-06-21 18:03:29
 */
public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
