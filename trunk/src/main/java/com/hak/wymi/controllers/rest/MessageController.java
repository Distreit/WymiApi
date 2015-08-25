package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.secure.SecureMessage;
import com.hak.wymi.persistance.pojos.unsecure.message.Message;
import com.hak.wymi.persistance.pojos.unsecure.message.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MessageController {
    @Autowired
    private MessageDao messageDao;

    @RequestMapping(
            value = "/message",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<List<SecureMessage>> getMessages(Principal principal) {
        List<Message> messages = messageDao.getAllReceived(principal);
        List<SecureMessage> secureTopics = messages.stream().map(SecureMessage::new).collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(secureTopics, HttpStatus.ACCEPTED);
    }

    @RequestMapping(
            value = "/message/{messageId}/alreadyRead",
            method = RequestMethod.PUT,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<SecureMessage> updateAlreadyRead(
            Principal principal,
            @RequestBody Boolean alreadyRead,
            @PathVariable Integer messageId) {
        Message message = messageDao.getReceived(principal, messageId);

        message.setAlreadyRead(alreadyRead);
        if (messageDao.update(message)) {
            return new ResponseEntity<>(new SecureMessage(message), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/message/{messageType}/{messageId}",
            method = RequestMethod.DELETE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<SecureMessage> deleteMessage(
            Principal principal,
            @Pattern(regexp = "(sent)|(recieved)}") @PathVariable String messageType,
            @PathVariable Integer messageId) {

        Message message;
        if ("sent".equals(messageType)) {
            message = messageDao.getSent(principal, messageId);
            message.setSourceDeleted(true);
        } else {
            message = messageDao.getReceived(principal, messageId);
            message.setDestinationDeleted(true);
        }

        if (messageDao.update(message)) {
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
