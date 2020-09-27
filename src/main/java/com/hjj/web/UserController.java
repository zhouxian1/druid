package com.hjj.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 前端控制器
 * 用户相关
 * @author zhouxian
 * @since 2019-5-29
 *
 * */
@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * 跳转登录页面
     * */

    @RequestMapping(value = "/goLogin",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public  String goLogin(){
        return  "/login";
    }

    /**
     * 用户登录
     * @auther zhoxuian
     * @since 2019-5-29
     *
     * */
    @RequestMapping(value = "/login",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public  String login(){
        return  "";
    }
    /**
     * 用户注册
     * @auther zhoxuian
     * @since 2019-5-29
     *
     * */
    @RequestMapping(value = "/register",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public  String register(){
        return  "";
    }

    /**
     * 退出登录
     * @auther zhoxuian
     * @since 2019-5-29
     *
     * */
    @RequestMapping(value = "/goOut",method = RequestMethod.GET,produces = {"application/json;charset=UTF-8"})
    public String loginOut(){
        return  "";
    }
}
