package com.shentu.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shentu.reggie.common.CustomException;
import com.shentu.reggie.dto.SetmealDto;
import com.shentu.reggie.entity.Setmeal;
import com.shentu.reggie.entity.SetmealDish;
import com.shentu.reggie.mapper.SetmealMapper;
import com.shentu.reggie.service.SetmealDishService;
import com.shentu.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        // 保存套餐基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);
        // 保存套餐和菜品的关联信息，操作setmeal_dish,执行insert
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        for (SetmealDish dish : dishes) {
            dish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(dishes);
    }

    /**
     * 删除套餐和关联数据
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态是否可以删除
        LambdaQueryWrapper<Setmeal> qw = new LambdaQueryWrapper<>();
        qw.in(Setmeal::getId, ids);
        qw.eq(Setmeal::getStatus, 1);
        int count = this.count(qw);
        // 如果不能删除，抛出异常
        if (count > 0) throw new CustomException("套餐正在售卖中无法删除");
        // 如果可以删除，先删除套餐表中的数据--- Setmeal
        this.removeByIds(ids);
        // 删除关系表的数据 setmeal_ dish
        LambdaQueryWrapper<SetmealDish> sdqw = new LambdaQueryWrapper<>();
        sdqw.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(sdqw);
    }

    /**
     * 获取某个套餐
     */
    public SetmealDto getWithDish(Long id) {
        // 查询套餐信息从Setmeal查
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        // 查套餐表 setmeal dish
        LambdaQueryWrapper<SetmealDish> sdqw = new LambdaQueryWrapper<>();
        sdqw.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(sdqw);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }


    /**
     * 更新某个套餐
     */
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        // 更新setmeal表基本信息
        this.updateById(setmealDto);
        // 清理当前菜
        LambdaQueryWrapper<SetmealDish> sdwq = new LambdaQueryWrapper<>();
        sdwq.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(sdwq);
        // 添加新加的
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }
}
