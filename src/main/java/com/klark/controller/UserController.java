package com.klark.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klark.async.email.InvitationEmailThread;
import com.klark.async.email.WorkQueue;
import com.klark.common.Constants;
import com.klark.common.EmailValidator;
import com.klark.common.ExecutionResult;
import com.klark.common.Mail;
import com.klark.user.model.User;
import com.klark.user.service.UserService;
import com.klark.util.CookiesUtil;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService contactService;

    @Autowired
    private Mail mail;

    private static WorkQueue emailQue = new WorkQueue(10, "invitation-announcement email");

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ExecutionResult getContact(@PathVariable Long id, ModelMap model) {
        ExecutionResult res = new ExecutionResult();
        User c = contactService.getUser(id);
        res.setMessage("success");
        res.setData(c);
        res.setStatusCode(200);
        return res;
    }

    @RequestMapping(value = "/reg", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    @com.wordnik.swagger.annotations.ApiOperation(value = "Create a connection", notes = "Create a connection", httpMethod = "PUT")
    @ApiErrors(value = { @ApiError(code = 500, reason = "Process error") })
    public @ResponseBody
    ExecutionResult addContact(@RequestBody User contact, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();
        populateHeader(request, response);

        if (!new EmailValidator().validate(contact.getEmail())) {
            res.setMessage("Not Valid Email Format!");
            res.setStatusCode(400);
            res.setData(contact);
        }
        User contactFromDB = contactService.isUserAlreadyExist(contact.getEmail());
        if (contactFromDB != null) {
            res.setMessage("User Already Exist");
            res.setStatusCode(400);
            res.setData(contactFromDB);
            return res;
        } else {
            User newUser = contactService.addUser(contact);
            res.setMessage("success");
            res.setData(newUser);
            res.setStatusCode(200);
            CookiesUtil.createSsoCookie(request, response, "UserAuth", String.valueOf(newUser.getId()));
            Map<String, String> statusMap = new HashMap<String, String>();
            statusMap.put("to", newUser.getEmail());
            statusMap.put("firstName", newUser.getFirstName());
            request.getSession().setAttribute(Constants.LOGGED_IN, newUser);
            sendContactAddedEmail(newUser.getId(), statusMap);
            return res;
        }
    }

    protected void sendContactAddedEmail(long userNumber, Map<String, String> statusMap) {
        emailQue.scheduleForExecution(new InvitationEmailThread(mail, statusMap));
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    ExecutionResult authenticate(@RequestBody User contact, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();

        User user = contactService.authenticated(contact.getEmail(), contact.getPassword());
        if (user != null) {
            request.getSession().setAttribute(Constants.LOGGED_IN, user);
            CookiesUtil.createSsoCookie(request, response, "UserAuth", String.valueOf(user.getId()));
            res.setMessage("true");
            res.setStatusCode(200);
            res.setData(user);
            return res;
        } else {
            res.setMessage("false");
            res.setStatusCode(401);
            res.setData(contact);
        }
        populateHeader(request, response);

        return res;
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    ExecutionResult update(@RequestBody User contact, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();

        User user = contactService.update(contact);
        if (user != null) {
            request.getSession().setAttribute(Constants.LOGGED_IN, user);
            CookiesUtil.createSsoCookie(request, response, "UserAuth", String.valueOf(user.getId()));
            res.setMessage("true");
            res.setStatusCode(200);
            res.setData(user);
            return res;
        } else {
            res.setMessage("false");
            res.setStatusCode(401);
            res.setData(contact);
        }
        populateHeader(request, response);

        return res;
    }

    private void populateHeader(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
        response.setHeader("Access-Control-Max-Age", "360");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

}
