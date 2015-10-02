package com.hak.wymi.persistance.pojos.message;

import com.hak.wymi.persistance.pojos.PersistentObject;
import com.hak.wymi.persistance.pojos.user.User;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "message")
public class Message extends PersistentObject {
    private static final long serialVersionUID = -7274116205201326082L;

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

    public Message() {
        super();
    }

    public Message(User destinationUser, User sourceUser, String subject, String content) {
        super();
        this.destinationUser = destinationUser;
        this.sourceUser = sourceUser;
        this.subject = subject;
        this.content = content;

        this.alreadyRead = Boolean.FALSE;
        this.destinationDeleted = Boolean.FALSE;
        this.sourceDeleted = Boolean.FALSE;
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

    public void setSourceDeleted(Boolean sourceDeleted) {
        this.sourceDeleted = sourceDeleted;
    }
}
