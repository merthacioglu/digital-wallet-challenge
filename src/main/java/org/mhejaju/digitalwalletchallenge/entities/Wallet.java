package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mhejaju.digitalwalletchallenge.entities.enums.Currency;

import java.math.BigDecimal;

@Data
@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private Customer customer;

    private Currency currency;

    @Column(name = "active_for_withdraw")
    private boolean activeForWithdraw;

    @Column(name = "active_for_shopping")
    private boolean activeForShopping;

    private BigDecimal balance;

    @Column(name = "usable_balance")
    private BigDecimal usableBalance;


}
