package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.pojos.secure.SecureMessage;
import com.hak.wymi.persistance.pojos.unsecure.Message;
import com.hak.wymi.persistance.pojos.unsecure.dao.MessageDao;
import com.hak.wymi.persistance.pojos.unsecure.interfaces.SecureToSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UniversalResponse> getMessages(Principal principal) {
        final List<SecureToSend> secureMessages = messageDao
                .getAllReceived(principal)
                .stream()
                .map(SecureMessage::new)
                .collect(Collectors.toCollection(LinkedList::new));

        return new ResponseEntity<>(new UniversalResponse().setData(secureMessages), HttpStatus.ACCEPTED);
    }

    @RequestMapping(
            value = "/message/{messageId}/alreadyRead",
            method = RequestMethod.PUT,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> updateAlreadyRead(
            Principal principal,
            @RequestBody Boolean alreadyRead,
            @PathVariable Integer messageId
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final Message message = messageDao.getReceived(principal, messageId);

        message.setAlreadyRead(alreadyRead);
        if (messageDao.update(message)) {
            return new ResponseEntity<>(universalResponse.setData(new SecureMessage(message)), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @RequestMapping(
            value = "/message/{messageType}/{messageId}",
            method = RequestMethod.DELETE,
            produces = "application/json; charset=utf-8")
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> deleteMessage(
            Principal principal,
            @PathVariable String messageType,
            @PathVariable Integer messageId
    ) {
        final UniversalResponse universalResponse = new UniversalResponse();

        Message message;
        if ("sent".equals(messageType)) {
            message = messageDao.getSent(principal, messageId);
            message.setSourceDeleted(Boolean.TRUE);
        } else {
            message = messageDao.getReceived(principal, messageId);
            message.setDestinationDeleted(Boolean.TRUE);
        }

        if (messageDao.update(message)) {
            return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(universalResponse.addUnknownError(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
