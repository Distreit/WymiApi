package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.BalanceManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.user.SecureCurrentUser;
import com.hak.wymi.utility.transactionprocessor.TransactionProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(value = "/currentUser")
public class CurrentUserController {
    @Autowired
    private UserManager userManager;

    @Autowired
    private TransactionProcessor transactionProcessor;

    @Autowired
    private BalanceManager balanceManager;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> getCurrentUser(Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final SecureCurrentUser secureUser = userManager.getSecureCurrent(principal);

        universalResponse.addTransactions(secureUser.getUserId(), transactionProcessor, balanceManager);

        return new ResponseEntity<>(universalResponse.setData(secureUser), HttpStatus.ACCEPTED);
    }

}
