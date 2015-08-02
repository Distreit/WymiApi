package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.validations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user")
@PasswordsMatch(groups = User.Registration.class)
@EmailsMatch(groups = User.Registration.class)
public class User {
    public static interface Registration {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @NotNull
    @Size(min = 3, max = 20, message = "Name must be between 3 and 30 characters in length")
    @NameDoesNotExist(groups = Registration.class)
    @Pattern(regexp = "[0-9a-zA-Z-_]*")
    private String name;

    @NotNull
    @Size(
            min = 8,
            max = 50,
            message = "Password must be between 8 and 256 characters in length",
            groups = Registration.class
    )
    @Password(groups = Registration.class)
    private String password;

    @Transient
    private String confirmPassword;

    private String roles;

    @NotNull
    @Email
    @EmailDoesNotExist(groups = Registration.class)
    private String email;

    @Transient
    private String confirmEmail;

    private Boolean validated = false;

    public User() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
    }

    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
}
