package com.studentmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLogger {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogger.class);

    public void log(String action) {
        logger.info("[AUDIT] {}", action);
    }
}
