package com.hak.wymi.persistance.pojos.smsmessage;

import com.hak.wymi.persistance.pojos.PersistentObject;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "smsmessage")
public class SMSMessage extends PersistentObject {
    private static final long serialVersionUID = -115203643613761156L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer smsMessageId;

    @NotNull
    private String number;

    @NotNull
    private String body;

    @NotNull
    private Boolean sent = false;

    private String errorCode;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime sentDate;

    public SMSMessage() {
        super();
    }

    public SMSMessage(String number, String body) {
        super();
        this.number = number;
        this.body = body;
    }

    public Integer getSmsMessageId() {
        return smsMessageId;
    }

    public void setSmsMessageId(Integer smsMessageId) {
        this.smsMessageId = smsMessageId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }

    public DateTime getSentDate() {
        return sentDate;
    }

    public void setSentDate(DateTime sentDate) {
        this.sentDate = sentDate;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
