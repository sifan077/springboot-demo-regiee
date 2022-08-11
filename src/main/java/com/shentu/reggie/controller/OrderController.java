/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/23
 * Time: 11:37
 * Describe:
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shentu.reggie.common.BaseContext;
import com.shentu.reggie.common.R;
import com.shentu.reggie.dto.OrdersDto;
import com.shentu.reggie.entity.OrderDetail;
import com.shentu.reggie.entity.Orders;
import com.shentu.reggie.entity.User;
import com.shentu.reggie.service.OrderDetailService;
import com.shentu.reggie.service.OrderService;
import com.shentu.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders) {
        orderService.submit(orders);
        return R.success("下单成功");
    }

    /**
     * 用户订单展示
     */
    @GetMapping("/userPage")
//    public R<Page<Orders>> userPage(int page, int pageSize) {
//        Page<Orders> pageInfo = new Page<>(page, pageSize);
//        LambdaQueryWrapper<Orders> wq = new LambdaQueryWrapper<>();
//        wq.eq(Orders::getUserId, BaseContext.getCurrentId());
//        wq.orderByDesc(Orders::getOrderTime);
//        orderService.page(pageInfo, wq);
//
//        return R.success(pageInfo);
//    }
    public R<Page<OrdersDto>> userPage(int page, int pageSize) {
        // 查询原来的订单
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wq = new LambdaQueryWrapper<>();
        wq.eq(Orders::getUserId, BaseContext.getCurrentId());
        wq.orderByDesc(Orders::getOrderTime);
        orderService.page(pageInfo, wq);
        // 扩展的订单
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(pageInfo, ordersDtoPage);
        // 获取原来的订单列表
        List<Orders> records = pageInfo.getRecords();
        // 扩展的订单列表
        List<OrdersDto> ordersDtos = new ArrayList<>();
        // 遍历原列表复制查询元素给扩展列表
        for (Orders item : records) {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            LambdaQueryWrapper<OrderDetail> odqw = new LambdaQueryWrapper<>();
            odqw.eq(OrderDetail::getOrderId, item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(odqw);
            ordersDto.setOrderDetails(orderDetails);
            ordersDtos.add(ordersDto);
        }
        // 扩展分页设置扩展类的列表
        ordersDtoPage.setRecords(ordersDtos);
        return R.success(ordersDtoPage);
    }

    /**
     * 商家查看订单
     *
     * @return
     */
    @GetMapping("/page")
//    public R<Page<Orders>> page(int page, int pageSize) {
//        Page<Orders> pageInfo = new Page<>(page, pageSize);
//        orderService.page(pageInfo);
//        return R.success(pageInfo);
//    }
    public R<Page<OrdersDto>> page(int page, int pageSize) {
        // 查询所有基本订单
        Page<Orders> pageInfo = new Page<>(page, pageSize);
        orderService.page(pageInfo);
        // 扩展订单
        Page<OrdersDto> dtoPage = new Page<>();
        // 复制属性
        BeanUtils.copyProperties(pageInfo, dtoPage);
        // 获取原订单
        List<Orders> records = pageInfo.getRecords();
        // 声明扩展的订单
        List<OrdersDto> ordersDtos = new ArrayList<>();
        // 遍历原订单查询，把属性给扩展订单
        for (Orders record : records) {
            OrdersDto item = new OrdersDto();
            BeanUtils.copyProperties(record, item);
            Long userId = record.getUserId();
            LambdaQueryWrapper<User> wq = new LambdaQueryWrapper<>();
            wq.eq(User::getId, userId);
            User user = userService.getOne(wq);
            item.setUserName(user.getName());
            ordersDtos.add(item);
        }
        // 扩展分页设置扩展订单列表
        dtoPage.setRecords(ordersDtos);
        return R.success(dtoPage);
    }

    /**
     * 派送订单
     */
    @PutMapping
    public R<String> delivery(@RequestBody Orders orders) {
        log.info("派送的订单:{}", orders);
        orderService.updateById(orders);
        return R.success("派送成功");
    }

}
