package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
public class Customer {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private String surname;

    @Column(name = "tckn")
    private int trIdentityNo;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY
            ,cascade = CascadeType.PERSIST, targetEntity = Wallet.class)
    private Set<Wallet> wallets;
}
