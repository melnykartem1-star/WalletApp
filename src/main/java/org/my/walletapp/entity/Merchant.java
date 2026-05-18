package org.my.walletapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Merchant {
    @Id
    private Long id;
}
