package com.hak.wymi.persistance.pojos.unsecure;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "destinationUserId")
    private User destinationUser;

    @ManyToOne
    @JoinColumn(name = "sourceUserId")
    private User sourceUser;

    private String subject;

    private String content;

    private Boolean alreadyRead;

    private Boolean destinationDeleted;

    private Boolean sourceDeleted;

    @Version
    private Integer version;

    private Date updated;

    private Date created;

    public Message(User destinationUser, User sourceUser, String subject, String content) {
        this.destinationUser = destinationUser;
        this.sourceUser = sourceUser;
        this.subject = subject;
        this.content = content;

        this.alreadyRead = Boolean.FALSE;
        this.destinationDeleted = Boolean.FALSE;
        this.sourceDeleted = Boolean.FALSE;
    }

    public Message() {
        // Needed for bean creation.
    }

    public void setSourceDeleted(Boolean sourceDeleted) {
        this.sourceDeleted = sourceDeleted;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public User getDestinationUser() {
        return destinationUser;
    }

    public void setDestinationUser(User destinationUser) {
        this.destinationUser = destinationUser;
    }

    public User getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(User sourceUser) {
        this.sourceUser = sourceUser;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getAlreadyRead() {
        return alreadyRead;
    }

    public void setAlreadyRead(Boolean alreadyRead) {
        this.alreadyRead = alreadyRead;
    }

    public Boolean getDestinationDeleted() {
        return destinationDeleted;
    }

    public void setDestinationDeleted(Boolean destinationDeleted) {
        this.destinationDeleted = destinationDeleted;
    }

    public Boolean getSourceDeleted() {
        return sourceDeleted;
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

    public Date getCreated() {
        return (Date) created.clone();
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
