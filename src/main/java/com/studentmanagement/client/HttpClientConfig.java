package com.studentmanagement.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(group = "notifications", types = NotificationServiceClient.class)
public class HttpClientConfig {
}