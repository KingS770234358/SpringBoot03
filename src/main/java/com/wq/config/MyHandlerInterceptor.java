package com.wq.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MyHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录成功之后 session中应该存储着用户的信息
        HttpSession session = request.getSession();
        String userName = (String) session.getAttribute("userName");
        if(userName!=null){

            return true;
        }
        request.setAttribute("msg","请先登录!");
        //response.sendRedirect("/index");
        request.getRequestDispatcher("/index.html").forward(request, response);
        return false;
    }
}
