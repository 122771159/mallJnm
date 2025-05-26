package com.jnm.mallJnm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MallJnmApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallJnmApplication.class, args);
    }

}
