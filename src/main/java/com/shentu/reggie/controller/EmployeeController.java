/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/21
 * Time: 11:36
 * Describe:
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shentu.reggie.common.R;
import com.shentu.reggie.entity.Employee;
import com.shentu.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     *
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("login: {}", employee);
        // 1.加密 页面提交的password
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 2. 查询数据库
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(qw);
        // 3. 判断是否存在
        if (emp == null) {
            return R.error("用户不存在，登陆失败");
        }
        // 4. 判断密码是否正确
        if (!password.equals(emp.getPassword())) {
            return R.error("密码错误，登陆失败");
        }
        // 5. 查看员工状态
        if (emp.getStatus() == 0) {
            return R.error("员工被禁用，登陆失败");
        }
        // 6. 登陆成功，把员工信息放入session、
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    /**
     * 退出登陆
     *
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        log.info("logout:{}", request.getSession().getAttribute("employee"));
        // 1. 清除session中保存的员工id
        request.getSession().removeAttribute("employee");
        // 2. 返回登出成功
        return R.success("退出成功");
    }

    /**
     * 新增员工
     *
     * @description
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        log.info("新增员工: {}", employee);
        // 设置初始密码，并使用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//
//        // 获取当前登录员工id
//        Long employeeId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(employeeId);
//        employee.setUpdateUser(employeeId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        log.info("分页查询员工信息，page: {}, pageSize: {}, name: {}", page, pageSize, name);

        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        // 构造条件构造器
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        // 添加一个过滤条件
        if (name != null && !"".equals(name)) {
            qw.like(Employee::getName, name);
        }
        // 排序
        qw.orderByDesc(Employee::getUpdateTime);
        // 查询
        employeeService.page(pageInfo, qw);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("修改员工信息，employee: {}", employee);
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empID = (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateUser(empID);
        employeeService.updateById(employee);
        return R.success("修改员工信息成功");
    }

    /**
     * 根据id查询员工信息
     *
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息，id: {}", id);
        Employee employee = employeeService.getById(id);
        return R.success(employee);
    }

}
