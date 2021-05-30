package com.wq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;

@Controller
public class LoginController {
    //@RequestMapping("/user/login/{username}")
    //public String login(@PathVariable("username") String username){
    @RequestMapping("/user/login")
    //public String login(@PathVariable("username") String username, @PathVariable("password") String password){
    //public String login(@RequestParam("username") String username, @RequestParam("password") String password){
    public String login(HttpServletRequest request, Model model){
        String name = request.getParameter("username");
        String password = request.getParameter("password");
        // System.out.println(name + password);
        if(!StringUtils.isEmpty(name) && "123456".equals(password)){
            request.getSession().setAttribute("userName",name);
            return "redirect:/main.html";
        }
        model.addAttribute("msg", "用户名或密码错误!");
        return "index";
    }
    // 注销用户
    @RequestMapping("/user/logout")
    public String logout(HttpSession session){
        // 这里删除的一定要跟上面设置的时候的键值一样
        session.removeAttribute("userName");
        return "redirect:/index.html";
    }
}
