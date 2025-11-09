package org.mhejaju.digitalwalletchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class WalletNotFoundException extends ResourceNotFoundException {
    public WalletNotFoundException(String customerId, String walletId) {
        super(String.format("No wallet with id: %s found belonging to the user with TR Identity Number: %s", walletId, customerId));
    }
}
