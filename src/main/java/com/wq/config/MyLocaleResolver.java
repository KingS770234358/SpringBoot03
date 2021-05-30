package com.wq.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Configuration
public class MyLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        // 利用传入的 httpServletRequest参数 解析请求(最好是通过解析请求头的方式)
        // 获取请求中的参数
        String langInfo =httpServletRequest.getParameter("l");
        Locale defaultLocale = Locale.getDefault();  // 得到默认的地区,如果没有l参数对应的值,就使用默认的地区
        // 如果接受到的关于语言(国际化)的参数不为空
        if(!StringUtils.isEmpty(langInfo)){
            // langInfo = zh_CN 或者 en_US ... 代表国家_地区
            // 要先按 下划线 _ 分割成 国家和地区
            String [] langInfosplit = langInfo.split("_"); //langInfosplit[0]国家  langInfosplit[1]地区
            // 根据上述的国家 和 地区的信息 创建自己的地区对象
            Locale useLocale = new Locale(langInfosplit[0], langInfosplit[1]);
            // 最后只要返回一个地区就可以了
            return useLocale;
        }
        // 若地区参数为空 则返回默认的地区就可以了
        return defaultLocale;
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
