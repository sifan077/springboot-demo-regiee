package com.shentu.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shentu.reggie.dto.SetmealDto;
import com.shentu.reggie.entity.Setmeal;

import java.util.List;

/**
 * 套餐(Setmeal)表服务接口
 *
 * @author shentu
 * @since 2022-06-21 18:04:12
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     *  删除套餐和关联数据
     */
    public void removeWithDish(List<Long> ids);

    /**
     *  获取某个套餐
     */
    public SetmealDto getWithDish(Long id);

    /**
     * 更新某个套餐
     */
    public void updateWithDish(SetmealDto setmealDto);

}
