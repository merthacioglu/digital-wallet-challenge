package org.mhejaju.digitalwalletchallenge.constants;

public class Regex {
    public static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$";

    public static final String TR_IDENTITY_NO_REGEX = "^\\d{11}$";
}
