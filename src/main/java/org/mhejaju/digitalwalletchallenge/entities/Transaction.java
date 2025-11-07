package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mhejaju.digitalwalletchallenge.entities.enums.OppositePartyType;
import org.mhejaju.digitalwalletchallenge.entities.enums.Status;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionType;

import java.math.BigDecimal;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OppositePartyType oppositePartyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status;

    @Column(nullable = false)
    private String oppositeParty;

    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;




}
