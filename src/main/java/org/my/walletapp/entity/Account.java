package org.my.walletapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.my.walletapp.enums.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "targetAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> inboundTransfers = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> outboundTransactions = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String currency;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now().withNano(0);
        }
    }
}
