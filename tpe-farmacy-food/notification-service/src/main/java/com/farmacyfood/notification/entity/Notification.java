package com.farmacyfood.notification.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notificaciones")
public class Notification {

    @Id
    private String id;
    private Long userId;
    private Long productId;
    private Long fridgeId;
    private String message;
    private Boolean read;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;

    public Notification() {}

    public Notification(Long userId, Long productId, Long fridgeId, String message) {
        this.userId = userId;
        this.productId = productId;
        this.fridgeId = fridgeId;
        this.message = message;
        this.read = false;
        this.sentAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Long getFridgeId() { return fridgeId; }
    public void setFridgeId(Long fridgeId) { this.fridgeId = fridgeId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getRead() { return read; }
    public void setRead(Boolean read) { this.read = read; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
