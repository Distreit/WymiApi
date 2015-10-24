package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.BalanceManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.balancetransaction.exceptions.InvalidValueException;
import com.hak.wymi.persistance.pojos.user.User;
import com.hak.wymi.utility.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class BalanceTransactionController {
    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private UserManager userManager;

    @Autowired
    private BalanceManager balanceManager;

    @RequestMapping(value = "/donation/{transactionId}", method = RequestMethod.DELETE, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> cancelCommentTransaction(Principal principal, @PathVariable int transactionId)
            throws InvalidValueException {
        final User user = userManager.get(principal);
        transactionProcessor.cancel(user, transactionId);
        return new ResponseEntity<>(new UniversalResponse()
                .addTransactions(principal, user, transactionProcessor, balanceManager), HttpStatus.ACCEPTED);
    }
}
