package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.managers.CallbackCodeManager;
import com.hak.wymi.persistance.managers.UserManager;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserValidationController {
    @Autowired
    private CallbackCodeManager callbackCodeManager;

    @Autowired
    private UserManager userManager;

    @RequestMapping(value = "/user/{userName}/validate/{code}")
    public String validateUser(@PathVariable String userName, @PathVariable String code) {
        final CallbackCode callbackCode = callbackCodeManager.getFromUserName(userName, code, CallbackCodeType.VALIDATION);

        if (callbackCode != null) {
            final User user = callbackCode.getUser();
            user.setValidated(Boolean.TRUE);
            user.setRoles(user.getRoles() + ",ROLE_VALIDATED");
            userManager.update(user);
            callbackCodeManager.delete(callbackCode);
            return "redirect:/validated";
        }

        return "redirect:/";
    }
}
