/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/23
 * Time: 11:01
 * Describe:
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shentu.reggie.common.BaseContext;
import com.shentu.reggie.common.R;
import com.shentu.reggie.entity.ShoppingCart;
import com.shentu.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加菜品到购物车
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("shoppingCart:{}", shoppingCart);
        // 设置用户id，指定是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        // 查询当前菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, currentId);
        if (dishId != null) {
            // 添加到购物车的是菜品
            qw.eq(ShoppingCart::getDishId, dishId);
        } else {
            // 添加到购物车的是套餐
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(qw);
        if (one != null) {
            // 如果存在则加一
            Integer number = one.getNumber();
            one.setNumber(number + 1);
            shoppingCartService.updateById(one);
        } else {
            // 如果不存在则插入到购物车，数量默认是1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 查看购物车
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> wq = new LambdaQueryWrapper<>();
        wq.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        wq.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(wq);
        return R.success(shoppingCarts);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> wq = new LambdaQueryWrapper<>();
        wq.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(wq);
        return R.success("清空购物车成功");
    }

    /**
     * 删除商品
     */
    @PostMapping("sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart) {
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> qw = new LambdaQueryWrapper<>();
        qw.eq(ShoppingCart::getUserId, currentId);
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            qw.eq(ShoppingCart::getDishId, dishId);
        } else {
            qw.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        ShoppingCart one = shoppingCartService.getOne(qw);
        if (one.getNumber() == 1) shoppingCartService.removeById(one);
        else {
            one.setNumber(one.getNumber() - 1);
            shoppingCartService.updateById(one);
        }
        return R.success("减少成功");
    }
}
