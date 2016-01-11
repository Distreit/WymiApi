package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.interfaces.SecureToSend;
import com.hak.wymi.persistance.managers.BalanceTransactionManager;
import com.hak.wymi.persistance.managers.CommentDonationManager;
import com.hak.wymi.persistance.managers.PostDonationManager;
import com.hak.wymi.persistance.pojos.balancetransaction.BalanceTransaction;
import com.hak.wymi.persistance.pojos.comment.CommentCreation;
import com.hak.wymi.persistance.pojos.comment.CommentDonation;
import com.hak.wymi.persistance.pojos.post.PostCreation;
import com.hak.wymi.persistance.pojos.post.PostDonation;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user/name/{userName}")
public class UserTransactionsController {
    @Autowired
    private CommentDonationManager commentDonationManager;

    @Autowired
    private PostDonationManager postDonationManager;

    @Autowired
    private BalanceTransactionManager balanceTransactionManager;

    @RequestMapping(value = "/transactions/{transactionType}/{resourceType}", method = RequestMethod.GET, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> get(@PathVariable String userName,
                                                 @PathVariable String transactionType,
                                                 @PathVariable String resourceType,
                                                 @RequestParam(required = false, defaultValue = "0") Integer firstResult,
                                                 @RequestParam(required = false, defaultValue = "25") Integer maxResults,
                                                 Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final List<BalanceTransaction> transactions;

        final Class transactionTypeClass;

        if (resourceType.equals("comment")) {
            if (transactionType.equals("donation")) {
                transactionTypeClass = CommentDonation.class;
            } else {
                transactionTypeClass = CommentCreation.class;
            }
        } else {
            if (transactionType.equals("donation")) {
                transactionTypeClass = PostDonation.class;
            } else {
                transactionTypeClass = PostCreation.class;
            }
        }

        if (principal == null || !userName.equals(principal.getName())) {
        } else {
            transactions = balanceTransactionManager.getPrivateTransactions(transactionTypeClass, principal.getName(), firstResult, maxResults);
            universalResponse.setData(transactions.stream().map(Result::new).collect(Collectors.toList()));
        }

        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }


    private class Result implements SecureToSend {
        public int amount;
        public Object resource;
        public DateTime date;

        public Result(BalanceTransaction transaction) {
            this.amount = transaction.getAmount();
            this.date = transaction.getCreated();
            this.resource = transaction.getSecureValue();
        }
    }
}
