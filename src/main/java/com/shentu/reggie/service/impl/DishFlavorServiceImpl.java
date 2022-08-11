/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/22
 * Time: 9:08
 * Describe:
 */

package com.shentu.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shentu.reggie.entity.DishFlavor;
import com.shentu.reggie.mapper.DishFlavorMapper;
import com.shentu.reggie.service.DishFlavorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
