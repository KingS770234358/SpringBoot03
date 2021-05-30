package com.wq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyMVCConfiguration implements WebMvcConfigurer {

    // 1.全局视图路由映射:重写WebMvcConfigurer添加视图控制器的方法
    // 编写自己的首页的跳转控制器
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
        registry.addViewController("/index/").setViewName("index");
        registry.addViewController("/index.html").setViewName("index");
        registry.addViewController("/main.html").setViewName("dashboard");
    }
    // 4.添加自己的时间格式转换器

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new DateParser());
    }

    // 3.添加自己定义的拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // /** 拦截所有的请求
        // excludePathPattern 对于登录页面的请求不能被拦截
        //                    由登录页面提交过来的登录验证请求也不能被拦截
        //                    静态资源也不能被拦截
        registry.addInterceptor(new MyHandlerInterceptor())
                .addPathPatterns("/**").
                excludePathPatterns("/","/index","/index/","/index.html","/user/login","/css/**","/js/**","/img/**");
    }

    // 在WebMvcConfigurer中配置的解析器等等对象
    // 都要注入spring中才能生效
    // 2.注入自己编写的地区解析器 使国际化组件生效
    @Bean
    public LocaleResolver localeResolver(){
        return new MyLocaleResolver();
    }


}
