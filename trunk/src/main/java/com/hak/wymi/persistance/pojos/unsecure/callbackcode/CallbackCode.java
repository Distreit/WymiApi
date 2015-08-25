package com.hak.wymi.persistance.pojos.unsecure.callbackcode;

import com.hak.wymi.persistance.pojos.unsecure.user.User;

import javax.persistence.*;
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
        return created;
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
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }
}
