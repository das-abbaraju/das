package com.klark.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.klark.message.service.UserMessageService;
import com.klark.user.model.BeneficiaryDTO;
import com.klark.user.model.ReceivedMessage;
import com.klark.user.model.ScheduleorUnscheduleMessage;
import com.klark.user.model.User;
import com.klark.user.model.UserMessagesDTO;
import com.klark.user.service.UserService;
import com.klark.util.EmailUtil;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;

@Controller
@RequestMapping("/usermsg")
public class UserMessageController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMessageService userMessageService;

    @Autowired
    private BeneficiaryService beneficiaryService;

    @Autowired
    private UserMessageBeneficiaryService userMessageBeneficiaryService;

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
    ExecutionResult getUserMessage(@PathVariable Long id, ModelMap model) {
        ExecutionResult res = new ExecutionResult();
        User user = userService.getUser(id);
        List<UserMessagesDTO> userMessages = userMessageService.getUserMessagesDTOByUserId(user);
        res.setMessage("success");
        res.setData(userMessages);
        res.setStatusCode(200);
        return res;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    @com.wordnik.swagger.annotations.ApiOperation(value = "Create a connection", notes = "Create a connection", httpMethod = "PUT")
    @ApiErrors(value = { @ApiError(code = 500, reason = "Process error") })
    public @ResponseBody
    ExecutionResult addUserMessage(@RequestBody UserMessagesDTO userMessageDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();
        EmailUtil.populateHeader(request, response);
        User user = (User) request.getSession().getAttribute(Constants.LOGGED_IN);
        userMessageDto.getUserMessage().setUser(user);
        userMessageService.addUserMessage(user, userMessageDto);
        List<UserMessagesDTO> userMessages = userMessageService.getUserMessagesDTOByUserId(user);

        res.setMessage("success");
        res.setData(userMessages);
        res.setStatusCode(200);
        return res;
    }

    @RequestMapping(value = "/accountview/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ExecutionResult getAccountView(@PathVariable Long id, ModelMap model) {
        ExecutionResult res = new ExecutionResult();
        AccountView accountView = new AccountView();

        User user = userService.getUser(id);
        List<String> subjectLines = userMessageService.getSubjectLines(user, 3);

        List<BeneficiaryDTO> beneficiaries = beneficiaryService.getBeneficiariesByUserId(user);
        accountView.setBeneficiaries(beneficiaries);

        List<ReceivedMessage> receivedMsgs = userMessageBeneficiaryService.receivedMessages(id);
        accountView.setReceivedMsgs(receivedMsgs);

        List<ScheduleorUnscheduleMessage> scheduleduserMsgs = userMessageBeneficiaryService.getScheduledMessages(user);
        accountView.setScheduledMsgs(scheduleduserMsgs);

        List<ScheduleorUnscheduleMessage> unScheduleduserMsgs = userMessageBeneficiaryService.getUnScheduledMessages(user);

        List<ScheduleorUnscheduleMessage> outGoingMsgs = new ArrayList<ScheduleorUnscheduleMessage>();
        outGoingMsgs.addAll(scheduleduserMsgs);
        outGoingMsgs.addAll(unScheduleduserMsgs);

        accountView.setOutGoingMsgs(outGoingMsgs);

        accountView.setCountBeneficiaries(beneficiaries != null ? beneficiaries.size() : 0);
        accountView.setCountReceivedMsgs(receivedMsgs != null ? receivedMsgs.size() : 0);
        int schdueldMsgsC = scheduleduserMsgs != null ? scheduleduserMsgs.size() : 0;
        int unScheduleduserMsgsC = unScheduleduserMsgs != null ? unScheduleduserMsgs.size() : 0;
        accountView.setCountOutGoingMsgs(schdueldMsgsC + unScheduleduserMsgsC);

        accountView.setSubjectLines(subjectLines);
        accountView.setUser(user);

        res.setMessage("success");
        res.setData(accountView);
        res.setStatusCode(200);
        return res;
    }

    protected void sendContactAddedEmail(long userNumber, Map<String, String> statusMap) {
        emailQue.scheduleForExecution(new InvitationEmailThread(mail, statusMap));
    }

    @RequestMapping(value = "/auth", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    ExecutionResult authenticate(@RequestBody User contact, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();

        User contactAlreadyExist = userService.authenticated(contact.getEmail(), contact.getPassword());
        if (contactAlreadyExist != null) {
            res.setMessage("true");
            res.setStatusCode(200);
            res.setData(contactAlreadyExist);
            return res;
        } else {
            res.setMessage("false");
            res.setStatusCode(401);
            res.setData(contact);
        }
        EmailUtil.populateHeader(request, response);

        return res;
    }

}
