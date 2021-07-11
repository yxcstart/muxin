package com.imooc.muxin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.imooc.muxin.mapper"})
@ComponentScan(basePackages = {"com.imooc.muxin", "com.imooc.muxin.idworker"})
public class MuxinApplication {

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }


    public static void main(String[] args) {
        SpringApplication.run(MuxinApplication.class, args);
    }

}
