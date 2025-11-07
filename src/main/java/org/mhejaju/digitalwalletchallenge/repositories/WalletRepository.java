package org.mhejaju.digitalwalletchallenge.repositories;

import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByCustomerId(Long customerId);
}
