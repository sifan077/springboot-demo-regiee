/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/22
 * Time: 20:40
 * Describe:
 */

package com.shentu.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shentu.reggie.common.R;
import com.shentu.reggie.entity.User;
import com.shentu.reggie.service.UserService;
import com.shentu.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();
        if (phone == null) return R.error("手机号错误");
        // 生成随机的4位验证码
        String validateCode = ValidateCodeUtils.generateValidateCode(4).toString();
        // 发送验证码
        // 假装自己发了
        // send(validateCode)
        log.info("validateCode:------------------>{}", validateCode);
        // 将生成的验证码存入session
        session.setAttribute(phone, validateCode);
        return R.success("手机验证码发送成功");
    }

    /**
     * 移动端登陆
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("map-------------->{}", map);
        // 获取手机号
        String phone = map.get("phone").toString();
        // 获取验证码
        String code = map.get("code").toString();
        // 获取session中验证码
        Object codeInSession = session.getAttribute(phone);
        // 进行验证码对比
        if (code != null && codeInSession.equals(code)) {
            // 如果正确才能登陆
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getPhone, phone);
            User user = userService.getOne(qw);
            if (user == null) {
                // 如果是新用户则注册，老用户登陆
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登陆失败,出现错误");
    }

    /**
     * 退出登陆
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
