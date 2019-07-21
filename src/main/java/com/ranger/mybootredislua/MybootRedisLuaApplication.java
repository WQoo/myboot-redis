package com.ranger.mybootredislua;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.ranger.mybootredislua")
public class MybootRedisLuaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybootRedisLuaApplication.class, args);
    }

}
