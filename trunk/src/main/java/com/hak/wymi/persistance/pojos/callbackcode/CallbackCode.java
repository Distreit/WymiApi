package com.hak.wymi.persistance.pojos.callbackcode;

import com.hak.wymi.persistance.pojos.AbstractPersistentObject;
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

@Entity
@Table(name = "callbackcode")
public class CallbackCode extends AbstractPersistentObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer callbackCodeId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private String code;

    @Enumerated(EnumType.STRING)
    private CallbackCodeType type;

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
}
