package com.hak.wymi.controllers.rest;

import com.hak.wymi.controllers.rest.helpers.Constants;
import com.hak.wymi.controllers.rest.helpers.UniversalResponse;
import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.PasswordChange;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/password")
public class PasswordController {
    @Autowired
    private UserManager userManager;

    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = Constants.JSON)
    public ResponseEntity<UniversalResponse> getSendPasswordChange(@Valid @RequestBody PasswordChange passwordChange) {
        final UniversalResponse universalResponse = new UniversalResponse();
        final CallbackCode callbackCode = callbackCodeManager.getFromCode(passwordChange.getCode(), CallbackCodeType.PASSWORD_RESET);

        final User user = callbackCode.getUser();
        user.setPassword(DigestUtils.sha256Hex(passwordChange.getPassword()));
        userManager.update(user);
        callbackCodeManager.delete(callbackCode);
        return new ResponseEntity<>(universalResponse, HttpStatus.ACCEPTED);
    }
}
