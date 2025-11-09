package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mhejaju.digitalwalletchallenge.entities.enums.Currency;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String walletId;

    @Column(unique = true)
    private String walletName;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY,
    cascade = CascadeType.PERSIST, targetEntity = Transaction.class)
    private Set<Transaction> transactions;

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
