package com.klark.controller;

import com.klark.common.Constants;
import com.klark.user.model.Contact;
import com.klark.util.CookiesUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping("/")
public class LoginController {


    @RequestMapping("/")
    public String auth(Map<String, Object> map) {
        return "login";
    }

    @RequestMapping("/login.view")
    public String listContacts(Map<String, Object> map) {
        return "login";
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public String loginWithError(Locale locale, Model model) {
        model.addAttribute("error", true);
        return "login";
    }

    @RequestMapping("/login")
    public String login(Map<String, Object> map) {
        map.put("contact", new Contact());
        return "login";
    }

    @RequestMapping("/logout")
    public String logout(Map<String, Object> map, HttpServletRequest request, HttpServletResponse response) {
        map.put("contact", new Contact());
        CookiesUtil.deleteCookie(Constants.LOGGED_IN_COOKIE, request, response);
        return "login";
    }

    @RequestMapping(value = "/myaccount.view", method = RequestMethod.GET)
    public String userProfile(@ModelAttribute("contact") Contact contact, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "myaccount";
    }

    @RequestMapping(value = "/onboard.view", method = RequestMethod.GET)
    public String onboard(@ModelAttribute("contact") Contact contact,BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return "onboard";
    }


    private String redirect(HttpServletRequest request, HttpServletResponse response, String page) throws Exception {
        if (request.getQueryString() != null) {
            response.sendRedirect("/site" + page + "?" + request.getQueryString());
        } else {
            response.sendRedirect("/site" + page);
        }
        return null;
    }
}
