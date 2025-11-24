package com.poetry.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.poetry.mapper")
public class MyBatisConfig {
}
