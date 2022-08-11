/*
 * Created by IntelliJ IDEA.
 * User: 思凡
 * Date: 2022/6/21
 * Time: 18:53
 * Describe : 自定义业务异常
 */

package com.shentu.reggie.common;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
