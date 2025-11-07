package org.mhejaju.digitalwalletchallenge.repositories;

import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long id);
}
