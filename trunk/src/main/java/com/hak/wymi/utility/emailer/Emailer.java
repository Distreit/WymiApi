package com.hak.wymi.utility.emailer;

import com.hak.wymi.persistance.managers.EmailManager;
import com.hak.wymi.persistance.pojos.email.Email;
import com.hak.wymi.utility.jsonconverter.JSONConverter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class Emailer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Emailer.class);

    @Autowired
    private EmailManager emailManager;

    @Autowired
    private JavaMailSender mailSender;

    private Queue<Email> emailQueue = new LinkedList<>();


    @Scheduled(fixedRate = 15000)
    public void checkEmailQueue() {
        if (emailQueue.isEmpty()) {
            emailQueue.addAll(emailManager.getUnsent());
        } else {
            send(emailQueue.remove());
        }
    }

    private void send(Email email) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email.getAddress());
        message.setSubject(email.getSubject());
        message.setText(email.getBody());
        try {
            mailSender.send(message);
            email.setSent(true);
            email.setSentDate(new DateTime());
            emailManager.update(email);
        } catch (MailException e) {
            LOGGER.error("Failed to send email. \n" + JSONConverter.getJSON(email, true), e);
        }
    }
}
