package org.mhejaju.digitalwalletchallenge.exceptions;

public class CustomerNotFoundException extends RuntimeException {
  public CustomerNotFoundException(String customerTrIdentityNo) {
    super(String.format("No customer found with the TR Identity No: %s", customerTrIdentityNo));
  }
}
