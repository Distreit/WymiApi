package com.hak.wymi.persistance.pojos.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.hak.wymi.persistance.interfaces.HasPassword;
import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.validations.Email;
import com.hak.wymi.validations.EmailDoesNotExist;
import com.hak.wymi.validations.EmailsMatch;
import com.hak.wymi.validations.NameDoesNotExist;
import com.hak.wymi.validations.Password;
import com.hak.wymi.validations.PasswordsMatch;
import com.hak.wymi.validations.groups.Creation;
import com.hak.wymi.validations.groups.Update;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "user")
@PasswordsMatch(groups = Creation.class)
@EmailsMatch(groups = Creation.class)
@NameDoesNotExist(groups = Creation.class)
public class User extends PersistentObject implements HasPassword {
    private static final long serialVersionUID = -6356099501014801727L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = {Creation.class, Update.class})
    private Integer userId;

    @NotNull
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters in length")
    @Pattern(regexp = "^[0-9a-zA-Z][0-9a-zA-Z-_]*$")
    private String name;

    @Transient
    @NotNull(groups = {Update.class})
    private String currentPassword;

    @NotNull(groups = {Creation.class})
    @Password(groups = {Creation.class, Update.class})
    private String password;

    @Transient
    private String confirmPassword;

    @Null(groups = {Creation.class, Update.class})
    private String roles;

    @NotNull(groups = {Creation.class})
    @Email
    @EmailDoesNotExist(groups = {Creation.class, Update.class})
    private String email;

    @Null(groups = {Creation.class})
    @Email
    @EmailDoesNotExist(groups = {Creation.class, Update.class})
    private String newEmail;

    @Transient
    private String confirmEmail;

    private Boolean validated = Boolean.FALSE;

    @ManyToMany(mappedBy = "subscribers")
    @JsonIgnore
    @Null(groups = {Update.class})
    private Set<Topic> subscriptions;

    @ManyToMany(mappedBy = "filters")
    @JsonIgnore
    @Null(groups = {Update.class})
    private Set<Topic> filters;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    @Null(groups = {Creation.class, Update.class})
    private Balance balance;

    @NotNull
    private Boolean willingJuror;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Null(groups = {Creation.class, Update.class})
    private DateTime lastJurored;

    @Override
    public boolean passwordsMatch() {
        return this.password.equals(this.confirmPassword);
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

    public Set<Topic> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Topic> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Set<Topic> getFilters() {
        return filters;
    }

    public void setFilters(Set<Topic> filters) {
        this.filters = filters;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    @JsonValue
    public SecureUser getSecureUser() {
        return new SecureUser(this);
    }

    public Boolean getWillingJuror() {
        return willingJuror;
    }

    public void setWillingJuror(Boolean willingJuror) {
        this.willingJuror = willingJuror;
    }

    public DateTime getLastJurored() {
        return lastJurored;
    }

    public void setLastJurored(DateTime lastJurored) {
        this.lastJurored = lastJurored;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
