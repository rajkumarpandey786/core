package com.rkp.topcore.core.observability;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "observability.logging")
public class AsyncLogConfig {

    private boolean asyncEnabled = true;

    private int queueSize = 65536;

    private int discardingThreshold = 0;

    private boolean includeCallerData = false;

    private String level = "INFO";

    private String directory = "logs";

    private String fileName = "gateway.log";

    private String auditFileName = "audit.log";

    private String performanceFileName = "performance.log";

    private int maxHistoryDays = 30;

    private String maxFileSize = "100MB";

    public boolean isAsyncEnabled() {
        return asyncEnabled;
    }

    public void setAsyncEnabled(boolean asyncEnabled) {
        this.asyncEnabled = asyncEnabled;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getDiscardingThreshold() {
        return discardingThreshold;
    }

    public void setDiscardingThreshold(int discardingThreshold) {
        this.discardingThreshold = discardingThreshold;
    }

    public boolean isIncludeCallerData() {
        return includeCallerData;
    }

    public void setIncludeCallerData(boolean includeCallerData) {
        this.includeCallerData = includeCallerData;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getAuditFileName() {
        return auditFileName;
    }

    public void setAuditFileName(String auditFileName) {
        this.auditFileName = auditFileName;
    }

    public String getPerformanceFileName() {
        return performanceFileName;
    }

    public void setPerformanceFileName(String performanceFileName) {
        this.performanceFileName = performanceFileName;
    }

    public int getMaxHistoryDays() {
        return maxHistoryDays;
    }

    public void setMaxHistoryDays(int maxHistoryDays) {
        this.maxHistoryDays = maxHistoryDays;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}