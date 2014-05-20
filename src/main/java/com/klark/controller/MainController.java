package com.klark.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.klark.user.service.UserService;

@Controller
@RequestMapping("/api")
public class MainController {

    @Autowired
    private UserService contactService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAPIList() {
        return "documentation";
    }

}
