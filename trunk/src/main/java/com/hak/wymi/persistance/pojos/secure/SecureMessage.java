package com.hak.wymi.persistance.pojos.secure;

import com.hak.wymi.persistance.pojos.unsecure.message.Message;

import java.util.Date;

public class SecureMessage {

    private String messageId;

    private String toUserName;

    private String fromUserName;

    private String subject;

    private String content;

    private boolean alreadyRead;

    private Date created;

    public SecureMessage(Message message) {
        this.messageId = message.getMessageId();
        this.toUserName = message.getDestinationUser().getName();
        if (message.getSourceUser() != null) {
            this.fromUserName = message.getSourceUser().getName();
        }
        this.subject = message.getSubject();
        this.content = message.getContent();
        this.alreadyRead = message.getAlreadyRead();
        this.created = message.getCreated();
    }

    public String getMessageId() {
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
        return created;
    }
}
