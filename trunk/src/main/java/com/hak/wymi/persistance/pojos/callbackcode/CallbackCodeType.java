package com.hak.wymi.persistance.pojos.callbackcode;

public enum CallbackCodeType {
    /**
     * Used to validate a new account.
     */
    VALIDATION,
    /**
     * Used to validate an existing accounts email change.
     */
    EMAIL_CHANGE,
    /**
     * Used when a user requests a password reset.
     */
    PASSWORD_RESET
}
