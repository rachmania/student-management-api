package com.studentmanagement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
class ContextInspectionConfig {

    private static final Logger logger = LoggerFactory.getLogger(ContextInspectionConfig.class);


    @Bean
    CommandLineRunner logBeanCount(ApplicationContext context) {
        return args -> {
            String[] beanNames = context.getBeanDefinitionNames();
            logger.info("ApplicationContext is managing {} beans", beanNames.length);

            List<String> studentBeans = Arrays.stream(beanNames)
                    .filter(name -> name.toLowerCase().contains("student"))
                    .sorted()
                    .toList();

            logger.info("Of those, {} are student-related:", studentBeans.size());
            studentBeans.forEach(name -> logger.info("  - {}", name));
        };
    }
}
