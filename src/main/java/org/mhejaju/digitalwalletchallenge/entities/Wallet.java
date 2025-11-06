package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mhejaju.digitalwalletchallenge.entities.enums.Currency;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String walletId;

    @Column(unique = true)
    private String walletName;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Currency currency;

    private boolean activeForWithdraw;

    private boolean activeForShopping;

    private BigDecimal balance;

    private BigDecimal usableBalance;

    @PrePersist
    private void generateWalletId() {
        if (this.walletId == null) {
            this.walletId = UUID.randomUUID().toString();
        }
    }


}
