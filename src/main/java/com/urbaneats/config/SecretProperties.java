package com.urbaneats.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "")
public class SecretProperties {
    private String dbHost;
    private String dbPass;
    private String firebaseApiKey;

    public String getDbHost() {
        return dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getFirebaseApiKey() {
        return firebaseApiKey;
    }

    public void setFirebaseApiKey(String firebaseApiKey) {
        this.firebaseApiKey = firebaseApiKey;
    }

    // getters and setters
}