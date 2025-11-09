package org.mhejaju.digitalwalletchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends ResourceNotFoundException {
    public TransactionNotFoundException(String customerId, String transactionId) {
        super(String.format("No transaction with id: %s found belonging to the user with TR Identity Number: %s", transactionId, customerId));
    }
}
