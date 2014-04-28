package com.picsauditing.employeeguard.controllers;

import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.employeeguard.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BetaFeedbackAction extends PicsRestActionSupport
{

  private static final Logger logger = LoggerFactory.getLogger(BetaFeedbackAction.class);
  @Autowired
  private EmailService emailService;

  public String feedbackComment() {

    String feedbackComment=this.getRequest().getParameter("feedbackComment");

    boolean status=emailService.sendEGFeedBackEmail(feedbackComment.toString());

    if(status)
      json.put("status", "SUCCESS");
    else
      json.put("status", "FAILURE");


    return JSON;
  }


}//--  BetaFeedbackAction