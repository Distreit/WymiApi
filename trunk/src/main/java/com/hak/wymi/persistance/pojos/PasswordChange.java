package com.hak.wymi.persistance.pojos;

import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCodeType;
import com.hak.wymi.validations.CallbackCodeExists;
import com.hak.wymi.validations.Password;
import com.hak.wymi.validations.PasswordsMatch;

import javax.validation.constraints.NotNull;

@PasswordsMatch
public class PasswordChange {

    @NotNull
    @Password
    private String password;

    @NotNull
    @Password
    private String confirmPassword;

    @NotNull
    @CallbackCodeExists(type = CallbackCodeType.PASSWORD_RESET)
    private String code;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
