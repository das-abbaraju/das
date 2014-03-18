package com.picsauditing.actions;

import com.picsauditing.mail.EmailRequestDTO;
import com.picsauditing.messaging.Publisher;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class PICS10116 extends PicsActionSupport {
	private String overrideEmail;
	private String templateId;
	private String invoiceId;

	public String getOverrideEmail() {
		return overrideEmail;
	}

	public void setOverrideEmail(String overrideEmail) {
		this.overrideEmail = overrideEmail;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String execute() throws Exception {
		if (
				StringUtils.isNotEmpty(getTemplateId())
						||
						StringUtils.isNotEmpty(getOverrideEmail())
						||
						StringUtils.isNotEmpty(getInvoiceId())
				) {

			Publisher emailRequestPublisher = SpringUtils.getBean(SpringUtils.EMAIL_REQUEST_PUBLISHER);

			EmailRequestDTO request = new EmailRequestDTO();
			if (StringUtils.isNotEmpty(getTemplateId())) {
				request.templateID = Integer.parseInt(getTemplateId());
			}

			if (StringUtils.isNotEmpty(getOverrideEmail())) {
				request.overrideEmail = getOverrideEmail();
			}

			if (StringUtils.isNotEmpty(getInvoiceId())) {
				request.invoiceID = Integer.parseInt(getInvoiceId());
			}

			emailRequestPublisher.publish(request);
		}

		return SUCCESS;
	}
}
