package org.mhejaju.digitalwalletchallenge.exceptions;

public class WalletNotAvailableException extends RuntimeException {
  public WalletNotAvailableException(String walletId, String unavailableFunctionality) {
    super(String.format("Wallet ID: %s is not available for: %s", walletId, unavailableFunctionality));
  }
}
