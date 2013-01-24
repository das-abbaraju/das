package com.picsauditing.actions.billing;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.Api;
import com.picsauditing.actions.PicsApiSupport;
import com.picsauditing.model.billing.CommissionModel;

public class ProcessCommission extends PicsApiSupport {

	@Autowired
	private CommissionModel commissionModel;

	private int invoiceId;

	private static final long serialVersionUID = -8251774107792967433L;

	private static final Logger logger = LoggerFactory.getLogger(ProcessCommission.class);

	@SuppressWarnings("unchecked")
	@Api
	@Override
	public String execute() {
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("invoice_id", invoiceId);

		try {
			commissionModel.processCommissionForInvoice(invoiceId);
			jsonResponse.put("status", "success");
		} catch (Exception e) {
			logger.error("An error occurred while processing the invoice commissions for invoiceId = {}", invoiceId, e);
			jsonResponse.put("status", "failure");
			jsonResponse.put("exception_message", e.getMessage());
		}

		json = jsonResponse;

		return JSON;
	}

	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

}
