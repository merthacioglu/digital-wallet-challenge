package org.mhejaju.digitalwalletchallenge.mapper;

import org.mhejaju.digitalwalletchallenge.dto.DepositDto;
import org.mhejaju.digitalwalletchallenge.entities.Transaction;
import org.mhejaju.digitalwalletchallenge.entities.enums.OppositePartyType;
import org.mhejaju.digitalwalletchallenge.entities.enums.TransactionType;

public class TransactionMapper {
    public static Transaction mapToTransaction(DepositDto depositDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(depositDto.amount());
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setOppositeParty(depositDto.source());
        transaction.setOppositePartyType(OppositePartyType.valueOf(depositDto.sourceType()));
        return transaction;
    }
}
