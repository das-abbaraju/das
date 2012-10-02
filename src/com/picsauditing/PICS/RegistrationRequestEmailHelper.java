package com.picsauditing.PICS;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.EmailAttachmentDAO;
import com.picsauditing.dao.EmailTemplateDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.EmailAttachment;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

public class RegistrationRequestEmailHelper {
	private static final Logger logger = LoggerFactory.getLogger(RegistrationRequestEmailHelper.class);

	@Autowired
	private ContractorRegistrationRequestDAO requestDAO;
	@Autowired
	private EmailAttachmentDAO attachmentDAO;
	@Autowired
	private EmailSender sender;
	@Autowired
	private EmailTemplateDAO templateDAO;

	private final int INITIAL_EMAIL = 83;

	private EmailBuilder builder = new EmailBuilder();

	public EmailQueue buildInitialEmail(ContractorRegistrationRequest request) throws Exception {
		if (request != null) {
			builder.addToken("newContractor", request);
			builder.setFromAddress(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS);
			builder.setToAddresses(request.getEmail());
			builder.setTemplate(templateDAO.find(INITIAL_EMAIL));

			return builder.build();
		}

		return null;
	}

	public OperatorForm getContractorLetterFromHierarchy(ContractorRegistrationRequest request) {
		if (request != null && request.getRequestedBy() != null) {
			Set<Integer> alreadyProcessed = new TreeSet<Integer>();

			OperatorAccount current = request.getRequestedBy();
			while (current != null && !alreadyProcessed.contains(current.getId())) {
				for (OperatorForm form : current.getOperatorForms()) {
					if (!Strings.isEmpty(form.getFormName()) && form.getFormName().contains("*")) {
						return form;
					}
				}

				alreadyProcessed.add(current.getId());
				current = current.getParent();
			}

			logger.info("Contractor letter not found for request {} (#{}) and requesting operator {} (#{})",
					new Object[] { request.getName(), request.getId(), request.getRequestedBy().getName(),
							request.getRequestedBy().getId() });
		}

		return null;
	}

	public void sendInitialEmail(ContractorRegistrationRequest request, String fileDirectory) throws Exception {
		EmailQueue email = buildInitialEmail(request);

		if (email != null) {
			sender.send(email);

			OperatorForm form = getContractorLetterFromHierarchy(request);

			if (form != null) {
				String filename = FileUtils.thousandize(form.getId()) + form.getFile();

				try {
					EmailAttachment attachment = new EmailAttachment();

					String absolutePath = fileDirectory + "/files/" + filename;
					File file = new File(absolutePath);

					byte[] bytes = new byte[(int) file.length()];
					FileInputStream fis = new FileInputStream(file);
					fis.read(bytes);

					attachment.setFileName(absolutePath);
					attachment.setContent(bytes);
					attachment.setFileSize((int) file.length());
					attachment.setEmailQueue(email);
					attachmentDAO.save(attachment);
				} catch (Exception e) {
					logger.error("Unable to open file: /files/{}", filename);
				}
			}
		}
	}
}
