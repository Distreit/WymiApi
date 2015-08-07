package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.unsecure.callbackcode.CallbackCodeType;
import com.hak.wymi.persistance.pojos.unsecure.user.User;
import com.hak.wymi.persistance.pojos.unsecure.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserValidationController {
    @Autowired
    CallbackCodeDao callbackCodeDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping(value = "/user/{userName}/validate/{code}")
    public String validateUser(@PathVariable String userName, @PathVariable String code) {
        CallbackCode callbackCode = callbackCodeDao.getFromUserName(userName, code, CallbackCodeType.VALIDATION);

        if (callbackCode != null) {
            User user = callbackCode.getUser();
            user.setValidated(true);
            if (userDao.update(user)) {
                callbackCodeDao.delete(callbackCode);
                return "redirect:/validated";
            }
        }

        return "redirect:/";
    }
}
