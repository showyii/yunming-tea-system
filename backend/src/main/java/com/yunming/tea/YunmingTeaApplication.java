package com.yunming.tea;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yunming.tea.mapper")
public class YunmingTeaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunmingTeaApplication.class, args);
    }
}
