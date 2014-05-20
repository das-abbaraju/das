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
import com.klark.common.EmailValidator;
import com.klark.common.ExecutionResult;
import com.klark.common.Mail;
import com.klark.message.service.BeneficiaryService;
import com.klark.user.model.BeneficiariesDTO;
import com.klark.user.model.Beneficiary;
import com.klark.user.model.BeneficiaryDTO;
import com.klark.user.model.User;
import com.klark.user.service.UserService;
import com.wordnik.swagger.annotations.ApiError;
import com.wordnik.swagger.annotations.ApiErrors;

@Controller
@RequestMapping("/beneficiary")
public class BeneficiaryController {

    @Autowired
    private UserService contactService;

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

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ExecutionResult getBeneficiary(@PathVariable Long id, ModelMap model) {
        ExecutionResult res = new ExecutionResult();
        Beneficiary b = beneficiaryService.getBeneficiaryById(id);
        res.setMessage("success");
        res.setData(b);
        res.setStatusCode(200);
        return res;
    }

    @RequestMapping(value = "/get/user/{id}", method = RequestMethod.GET)
    public @ResponseBody
    ExecutionResult getBeneficiaryByUserId(@PathVariable Long id, ModelMap model) {
        ExecutionResult res = new ExecutionResult();
        BeneficiariesDTO dto = new BeneficiariesDTO();

        List<BeneficiaryDTO> bList = beneficiaryService.getBeneficiariesByUserId(contactService.getUser(id));
        dto.setBeneficiaries(bList);
        dto.setCount(bList != null ? bList.size() : 0);
        res.setMessage("success");
        res.setData(dto);
        res.setStatusCode(200);
        return res;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON })
    @com.wordnik.swagger.annotations.ApiOperation(value = "Create a connection", notes = "Create a connection", httpMethod = "PUT")
    @ApiErrors(value = { @ApiError(code = 500, reason = "Process error") })
    public @ResponseBody
    ExecutionResult addBeneficiary(@RequestBody Beneficiary beneficiary, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ExecutionResult res = new ExecutionResult();
        User user = (User) request.getSession().getAttribute(Constants.LOGGED_IN);
        if (!new EmailValidator().validate(beneficiary.getEmail())) {
            res.setMessage("Not Valid Email Format!");
            res.setStatusCode(400);
            res.setData(beneficiary);
        }
        beneficiary.setUser(user);
        beneficiary = beneficiaryService.addBeneficiary(beneficiary);
        if (beneficiary != null) {
            res.setMessage("Success");
            res.setStatusCode(200);
            res.setData(beneficiary);
        } else {
            res.setMessage("Could not create Beneficiary");
            res.setStatusCode(500);
            res.setData(beneficiary);
        }

        return res;
    }

    protected void sendContactAddedEmail(long userNumber, Map<String, String> statusMap) {
        emailQue.scheduleForExecution(new InvitationEmailThread(mail, statusMap));
    }

}
