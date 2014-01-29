package com.picsauditing.employeeguard.messaging;

import com.picsauditing.employeeguard.services.EmployeeGuardServiceDisabler;
import com.picsauditing.messaging.MessageHandler;
import com.picsauditing.provisioning.ProductSubscriptionService;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EmployeeGuardHandler implements MessageHandler {
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeGuardHandler.class);

	@Autowired
	private EmployeeGuardServiceDisabler employeeGuardServiceDisabler;
	@Autowired
	private ProductSubscriptionService productSubscriptionService;

	@Override
	public void handle(List<Message> messages) {
		for (Message message : messages) {
			handle(message);
		}
	}

	@Override
	public void handle(Message message) {
		try {
			String msgBody = new String(message.getBody());
			JSONObject json = (JSONObject) new JSONParser().parse(msgBody);

			String command = json.get("command").toString();
			int accountId = NumberUtils.toInt(json.get("id").toString());

			if ("remove".equals(command)) {
				employeeGuardServiceDisabler.removeAccount(accountId);
				productSubscriptionService.removeEmployeeGUARD(accountId);
			} else {
				productSubscriptionService.addEmployeeGUARD(accountId);
			}


			// Pass information to employeeguard account service?
		} catch (Exception e) {
			LOG.error("Error parsing message: {}\n{}", message.getBody(), e);
		}
	}

}
