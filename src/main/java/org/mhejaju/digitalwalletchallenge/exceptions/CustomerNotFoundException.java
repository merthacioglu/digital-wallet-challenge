package org.mhejaju.digitalwalletchallenge.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CustomerNotFoundException extends ResourceNotFoundException {
  public CustomerNotFoundException(String customerTrIdentityNo) {
    super(String.format("No customer found with the TR Identity No: %s", customerTrIdentityNo));
  }
}
