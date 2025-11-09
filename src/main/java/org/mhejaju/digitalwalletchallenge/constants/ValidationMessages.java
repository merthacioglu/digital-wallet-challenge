package org.mhejaju.digitalwalletchallenge.constants;

public class ValidationMessages {
    public static final String NAME_SIZE_MISMATCH = "Name must be between 2 and 50 characters";
    public static final String SURNAME_SIZE_MISMATCH = "Surname must be between 2 and 50 characters";
    public static final String NAME_REQUIRED = "Wallet name must be provided";
    public static final String SURNAME_REQUIRED = "Surname must be provided";
    public static final String CURRENCY_REQUIRED = "Currency must be provided";
    public static final String CURRENCY_INVALID = "Currency must be either USD, EUR or TRY";
    public static final String TR_IDENTITY_NO_INVALID = "TR Identity Number must contain exactly 11 digits";
    public static final String TR_IDENTITY_NO_REQUIRED = "TR Identity Number must be provided";
    public static final String EMAIL_REQUIRED = "Email address must be provided";
    public static final String EMAIL_INVALID = "Invalid email format";
    public static final String AMOUNT_REQUIRED = "Amount must be provided";
    public static final String ERR_MIN_AMOUNT = "Amount must be greater than 0";
    public static final String ERR_MAX_AMOUNT = "Amount must be less than 10000";
    public static final String WALLET_ID_REQUIRED = "Wallet ID must be provided";
    public static final String IBAN_OR_PAYMENT_ID_REQUIRED = "IBAN or payment ID must be provided";
    public static final String SOURCE_TYPE_REQUIRED = "Source type must be provided";
    public static final String SOURCE_TYPE_INVALID = "Source type must have value of either 'IBAN' or 'PAYMENT'";
    public static final String DESTINATION_TYPE_REQUIRED = "Destination type must be provided";
    public static final String DESTINATION_TYPE_INVALID = "Destination type must have value of either 'IBAN' or 'PAYMENT'";
    public static final String STATUS_REQUIRED = "Transaction status must be provided";
    public static final String STATUS_INVALID = "Transaction status must be either 'APPROVED' or 'DENIED'";
    public static final String TRANSACTION_ID_REQUIRED = "Transaction ID must be provided";
    public static final String PASSWORD_INVALID = """
            Password must contain at least 8 characters and:
            At least 1 lowercase letter
            At least 1 uppercase letter
            At least 1 digit
            At least one special character""";

    public static final String PASSWORD_REQUIRED = "Password must be provided";


}
