package org.mhejaju.digitalwalletchallenge.constants;

public class ValidationMessages {
    public static final String NAME_SIZE_MISMATCH = "Name must be between 2 and 50 characters";
    public static final String SURNAME_SIZE_MISMATCH = "Surname must be between 2 and 50 characters";
    public static final String NAME_REQUIRED = "Wallet name must be provided";
    public static final String SURNAME_REQUIRED = "Surname must be provided";
    public static final String INCORRECT_CURRENCY_ERROR = "Currency must be either USD, EUR or TRY";
    public static final String TR_IDENTITY_NO_FORMAT_ERROR = "TR Identity Number must contain exactly 11 digits";
    public static final String TR_IDENTITY_NO_REQUIRED = "TR Identity Number must be provided";
    public static final String EMAIL_REQUIRED = "Email address must be provided";
    public static final String EMAIL_FORMAT_ERROR = "Invalid email format";

    public static final String PASSWORD_FORMAT_ERROR = "Password must contain at least 8 characters and: \n" +
            "At least 1 lowercase letter \n" +
            "At least 1 uppercase letter \n" +
            "At least 1 digit \n" +
            "At least one special character";

    public static final String PASSWORD_REQUIRED = "Password must be provided";


}
