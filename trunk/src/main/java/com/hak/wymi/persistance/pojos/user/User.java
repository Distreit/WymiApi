package com.hak.wymi.persistance.pojos.user;

import com.hak.wymi.persistance.interfaces.HasPassword;
import com.hak.wymi.persistance.interfaces.HasPointsBalance;
import com.hak.wymi.persistance.pojos.topic.Topic;
import com.hak.wymi.validations.Email;
import com.hak.wymi.validations.EmailDoesNotExist;
import com.hak.wymi.validations.EmailsMatch;
import com.hak.wymi.validations.NameDoesNotExist;
import com.hak.wymi.validations.Password;
import com.hak.wymi.validations.PasswordsMatch;
import com.hak.wymi.validations.groups.Creation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "user")
@PasswordsMatch(groups = Creation.class)
@EmailsMatch(groups = Creation.class)
@NameDoesNotExist(groups = Creation.class)
public class User implements HasPassword, HasPointsBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Creation.class)
    private Integer userId;

    @NotNull
    @Size(min = 3, max = 30, message = "Name must be between 3 and 30 characters in length")
    @Pattern(regexp = "[0-9a-zA-Z-_]*")
    private String name;

    @NotNull
    @Size(
            min = 8,
            max = 50,
            message = "Password must be between 8 and 50 characters in length",
            groups = Creation.class
    )
    @Password(groups = Creation.class)
    private String password;

    @Transient
    private String confirmPassword;

    @Null(groups = Creation.class)
    private String roles;

    @NotNull
    @Email
    @EmailDoesNotExist(groups = Creation.class)
    private String email;

    @Transient
    private String confirmEmail;

    private Boolean validated = Boolean.FALSE;

    @ManyToMany(mappedBy = "subscribers")
    private Set<Topic> subscriptions;

    @ManyToMany(mappedBy = "filters")
    private Set<Topic> filters;

    @Version
    @Null(groups = Creation.class)
    private Integer version;

    @Null(groups = Creation.class)
    private Date created;

    @Null(groups = Creation.class)
    private Date updated;

    @Override
    public boolean passwordsMatch() {
        return this.password.equals(this.confirmPassword);
    }

    @Override
    public boolean addPoints(Integer amount) {
        return true;
    }

    @Override
    public boolean removePoints(Integer amount) {
        return true;
    }

    @Override
    public void incrementTransactionCount() {
        // Doesn't need to track this.
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getCreated() {
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return (Date) updated.clone();
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
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
}
