package com.hak.wymi.persistance.pojos.message;

import com.hak.wymi.persistance.interfaces.SecureToSend;

import java.util.Date;

public class SecureMessage implements SecureToSend {

    private final Integer messageId;

    private final String toUserName;

    private final String fromUserName;

    private final String subject;

    private final String content;

    private final boolean alreadyRead;

    private final Date created;

    public SecureMessage(Message message) {
        this.messageId = message.getMessageId();
        this.toUserName = message.getDestinationUser().getName();
        if (message.getSourceUser() == null) {
            this.fromUserName = "";
        } else {
            this.fromUserName = message.getSourceUser().getName();
        }
        this.subject = message.getSubject();
        this.content = message.getContent();
        this.alreadyRead = message.getAlreadyRead();
        this.created = message.getCreated();
    }

    public Integer getMessageId() {
        return messageId;
    }

    public String getToUserName() {
        return toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public boolean isAlreadyRead() {
        return alreadyRead;
    }

    public Date getCreated() {
        return (Date) created.clone();
    }
}
