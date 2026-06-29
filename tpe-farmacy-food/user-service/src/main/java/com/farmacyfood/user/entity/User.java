package com.farmacyfood.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String authUsername;

    @ElementCollection
    @CollectionTable(name = "user_dietary_preferences",
            joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preference")
    private List<String> dietaryPreferences;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public User(String name, String email, String authUsername, List<String> dietaryPreferences) {
        this.name = name;
        this.email = email;
        this.authUsername = authUsername;
        this.dietaryPreferences = dietaryPreferences;
    }
}
