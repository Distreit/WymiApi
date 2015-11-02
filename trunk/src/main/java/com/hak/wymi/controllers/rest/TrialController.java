package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.TrialManager;
import com.hak.wymi.persistance.pojos.trial.Trial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TrialController {

    @Autowired
    private TrialManager trialManager;

    @RequestMapping(value = "/trial", method = RequestMethod.GET, produces = Constants.JSON)
    @PreAuthorize("hasRole('ROLE_VALIDATED')")
    public ResponseEntity<UniversalResponse> getAccused(Principal principal) {
        final UniversalResponse universalResponse = new UniversalResponse();

        final Trial trial = trialManager.get(principal.getName());
        if (trial != null) {
            return new ResponseEntity<>(universalResponse.setData(trial), HttpStatus.ACCEPTED);
        }
        return new ResponseEntity<>(universalResponse, HttpStatus.NO_CONTENT);
    }
}
