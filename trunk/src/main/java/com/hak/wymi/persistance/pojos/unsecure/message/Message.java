package com.hak.wymi.persistance.pojos.unsecure.message;

import com.hak.wymi.persistance.pojos.unsecure.user.User;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String messageId;

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

        this.alreadyRead = false;
        this.destinationDeleted = false;
        this.sourceDeleted = false;
    }

    public Message() {
    }

    public void setSourceDeleted(Boolean sourceDeleted) {
        this.sourceDeleted = sourceDeleted;
    }
}
