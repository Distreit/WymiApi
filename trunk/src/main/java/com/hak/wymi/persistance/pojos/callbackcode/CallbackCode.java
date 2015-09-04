package com.hak.wymi.persistance.pojos.callbackcode;

import com.hak.wymi.persistance.pojos.user.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Table(name = "callbackcode")
public class CallbackCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer callbackCodeId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String code;

    @Enumerated(EnumType.STRING)
    private CallbackCodeType type;

    @Version
    private Integer version;

    private Date created;

    private Date updated;

    public Integer getCallbackCodeId() {
        return callbackCodeId;
    }

    public void setCallbackCodeId(Integer callbackCodeId) {
        this.callbackCodeId = callbackCodeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userId) {
        this.user = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CallbackCodeType getType() {
        return type;
    }

    public void setType(CallbackCodeType type) {
        this.type = type;
    }

    public Date getCreated() {
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Date getUpdated() {
        return (Date) updated.clone();
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
