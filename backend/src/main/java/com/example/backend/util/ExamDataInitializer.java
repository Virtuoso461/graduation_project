package com.example.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * 测试数据初始化工具，仅在开发环境中使用
 */
@Configuration
@Profile("dev") // 仅在开发环境中启用
public class ExamDataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(ExamDataInitializer.class);
    
    /**
     * 在应用启动时执行 SQL 脚本初始化测试数据
     */
    @Bean
    public CommandLineRunner initExamData(DataSource dataSource) {
        return args -> {
            logger.info("开始初始化测试数据...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db/init-exam-data.sql"));
                populator.execute(dataSource);
                logger.info("测试数据初始化完成");
            } catch (Exception e) {
                logger.error("初始化测试数据时发生错误", e);
            }
        };
    }
} 