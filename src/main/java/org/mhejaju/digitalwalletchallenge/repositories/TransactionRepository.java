package org.mhejaju.digitalwalletchallenge.repositories;

import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long id);

    Optional<Transaction> findByTransactionId(String transactionId);


}
