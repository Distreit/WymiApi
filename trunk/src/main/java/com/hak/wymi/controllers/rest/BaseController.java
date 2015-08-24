package com.hak.wymi.controllers.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

/**
 * Handles requests for the application home page.
 */
@Controller
public class BaseController {
    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    private BaseController() {
    }
}
