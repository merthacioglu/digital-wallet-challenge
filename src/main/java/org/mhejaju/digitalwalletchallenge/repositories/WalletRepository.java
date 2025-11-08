package org.mhejaju.digitalwalletchallenge.repositories;

import org.mhejaju.digitalwalletchallenge.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findByCustomerId(Long customerId);
    Optional<Wallet> findByWalletId(String walletId);
}
