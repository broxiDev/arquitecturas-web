package com.farmacyfood.notification.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "suscripciones")
public class Subscription {

    @Id
    private String id;
    private Long userId;
    private String deviceToken;
    private List<Long> productPreferences;
    private List<Long> heladeraIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Subscription() {}

    public Subscription(Long userId, String deviceToken, List<Long> productPreferences, List<Long> heladeraIds) {
        this.userId = userId;
        this.deviceToken = deviceToken;
        this.productPreferences = productPreferences;
        this.heladeraIds = heladeraIds;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeviceToken() { return deviceToken; }
    public void setDeviceToken(String deviceToken) { this.deviceToken = deviceToken; }

    public List<Long> getProductPreferences() { return productPreferences; }
    public void setProductPreferences(List<Long> productPreferences) { this.productPreferences = productPreferences; }

    public List<Long> getHeladeraIds() { return heladeraIds; }
    public void setHeladeraIds(List<Long> heladeraIds) { this.heladeraIds = heladeraIds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
