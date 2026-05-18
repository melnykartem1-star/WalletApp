package org.my.walletapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Merchant> merchants = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String locale = "en-GB";

    private ZoneId timezone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_logon", nullable = false)
    private LocalDateTime lastLogin;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.lastLogin == null) {
            this.lastLogin = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastLogin = LocalDateTime.now().withNano(0);
    }

    public User(String name, String email, String password, String locale, ZoneId timezone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.locale = locale != null ? locale : "en-GB";
        this.timezone = timezone;
    }
}