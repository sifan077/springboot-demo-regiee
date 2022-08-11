package com.shentu.reggie.dto;

import com.shentu.reggie.entity.Setmeal;
import com.shentu.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
