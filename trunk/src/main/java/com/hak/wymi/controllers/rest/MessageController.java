package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.secure.SecureMessage;
import com.hak.wymi.persistance.pojos.unsecure.message.Message;
import com.hak.wymi.persistance.pojos.unsecure.message.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class MessageController extends BaseController {
    @Autowired
    private MessageDao messageDao;

    @RequestMapping(
            value = "/message",
            method = RequestMethod.GET,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<List<SecureMessage>> getMessages(Principal principal) {
        List<Message> messages = messageDao.getIncoming(principal);
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

        Message message = messageDao.get(principal, messageId);
        if (alreadyRead == null) {
            alreadyRead = false;
        }
        message.setAlreadyRead(alreadyRead);
        if (messageDao.update(message)) {
            return new ResponseEntity<>(new SecureMessage(message), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
