package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mhejaju.digitalwalletchallenge.entities.enums.OppositePartyType;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionStatus;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionType;

import java.math.BigDecimal;
import java.security.SecureRandom;

import static org.mhejaju.digitalwalletchallenge.constants.Miscellaneous.ALPHANUMERIC;

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
    private TransactionStatus status;

    @Column(nullable = false)
    private String oppositeParty;

    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    @Column(nullable = false)
    private BigDecimal amount;

    @PrePersist
    private void generateTransactionId() {
        if (this.transactionId == null) {
            this.transactionId = generateUniqueTransactionId();
        }
    }


    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TRANSACTION_ID_LENGTH = 12;

    private String generateUniqueTransactionId() {
        StringBuilder sb = new StringBuilder(TRANSACTION_ID_LENGTH);
        for (int i = 0; i < TRANSACTION_ID_LENGTH; i++) {
            sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return sb.toString();
    }




}
