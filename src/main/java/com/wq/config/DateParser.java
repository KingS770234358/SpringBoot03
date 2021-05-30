package com.wq.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser implements Converter<String, Date> {
    @Override
    public Date convert(String source) {

        if(source.length()>=8&&source.length()<=10){
            source+=" 00:00:00";
        }
        // System.out.println("时间格式转换器"+source);
        source = source.replace('/','-');
        Date target = null;
        if(!StringUtils.isEmpty(source)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                target =  format.parse(source);
            } catch (ParseException e) {
                throw new RuntimeException(String.format("parser %s to Date fail", source));
            }
        }
        return target;
    }
}
