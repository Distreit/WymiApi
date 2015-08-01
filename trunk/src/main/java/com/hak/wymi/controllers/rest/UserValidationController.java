package com.hak.wymi.controllers.rest;

import com.hak.wymi.persistance.pojos.callbackcode.CallbackCode;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeDao;
import com.hak.wymi.persistance.pojos.callbackcode.CallbackCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserValidationController {
    @Autowired
    CallbackCodeDao callbackCodeDao;

    @RequestMapping(value = "/user/{userName}/validate/{code}")
    public String validateUser(@PathVariable String userName, @PathVariable String code) {
        CallbackCode callbackCode = callbackCodeDao.getFromUserName(userName, code, CallbackCodeType.VALIDATION);

        return "redirect:/validated";
    }
}
