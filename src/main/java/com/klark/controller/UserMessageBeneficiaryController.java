package com.klark.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
import com.klark.common.ExecutionResult;
import com.klark.common.Mail;
import com.klark.message.service.BeneficiaryService;
import com.klark.message.service.UserMessageBeneficiaryService;
import com.klark.user.model.User;
import com.klark.user.model.UserMessageBeneficiary;
import com.klark.user.service.UserService;
import com.klark.util.EmailUtil;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;

@Controller
@RequestMapping("/usermsgBeneficiary")
public class UserMessageBeneficiaryController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMessageBeneficiaryService userMessageService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private Mail mail;

    private static WorkQueue emailQue = new WorkQueue(10, "invitation-announcement email");

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/get/{uid}", method = RequestMethod.GET)
    public @ResponseBody
    ExecutionResult getUserMessageByUserId(@PathVariable Long uid, ModelMap model) {
        ExecutionResult res = new ExecutionResult();
        List<UserMessageBeneficiary> userMessage = userMessageService.getUserBeneficiarysByUserId(userService.getUser(uid));
        res.setMessage("success");
        res.setData(userMessage);
        res.setStatusCode(200);
        return res;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    @com.wordnik.swagger.annotations.ApiOperation(value = "Create a connection", notes = "Create a connection", httpMethod = "PUT")
    @ApiErrors(value = { @ApiError(code = 500, reason = "Process error") })
    public @ResponseBody
    ExecutionResult addUserMessage(@RequestBody UserMessageBeneficiary userMessage, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();
        EmailUtil.populateHeader(request, response);
        User user = (User) request.getSession().getAttribute(Constants.LOGGED_IN);
        userMessage.setUser(user);
        UserMessageBeneficiary userMsg = userMessageService.addUserBeneficiary(userMessage);

        if (userMsg.getId() != 0) {
            res.setMessage("success");
            res.setData(userMsg);
            res.setStatusCode(200);
        }
        return res;
    }

    protected void sendContactAddedEmail(long userNumber, Map<String, String> statusMap) {
        emailQue.scheduleForExecution(new InvitationEmailThread(mail, statusMap));
    }

}
