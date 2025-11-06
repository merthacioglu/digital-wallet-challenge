package org.mhejaju.digitalwalletchallenge.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.mhejaju.digitalwalletchallenge.entities.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Customer implements UserDetails {

    @Id
    @Column(name = "customer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Column(name = "tckn", nullable = false, unique = true)
    private String trIdentityNo;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;


    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY
            ,cascade = CascadeType.PERSIST, targetEntity = Wallet.class)
    private Set<Wallet> wallets;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
