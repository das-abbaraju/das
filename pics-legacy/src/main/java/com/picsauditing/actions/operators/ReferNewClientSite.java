package com.picsauditing.actions.operators;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.ClientSiteReferral;
import com.picsauditing.jpa.entities.ClientSiteReferralStatus;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReferNewClientSite extends PicsActionSupport {
	@Autowired
	protected BasicDAO basicDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected EmailSender emailSenderSpring;

	private ClientSiteReferral newClientSite;
	private ClientSiteReferralStatus status = ClientSiteReferralStatus.Active;

	private String addToNotes;

	private String contactType;

	private static final int CLIENT_SITE_REFERRAL_NOTIFICATION_EMAIL = 226;
	public static final String EMAIL = "Personal Email";
	public static final String PHONE = "Phone";

	public String execute() {
		if (newClientSite == null || newClientSite.getId() == 0) {
			newClientSite = new ClientSiteReferral();
		} else {
			status = newClientSite.getStatus();
		}

		return SUCCESS;
	}

	public String save() throws Exception {
		checkSourceFields();
		checkReferenceFields();

		// There are errors, just exit out
		if (getActionErrors().size() > 0)
			return SUCCESS;

		newClientSite.setAuditColumns(permissions);

		if (newClientSite.getId() == 0) {
			newClientSite.setStatus(ClientSiteReferralStatus.Active);

			try {
				newClientSite = (ClientSiteReferral) basicDAO.save(newClientSite);
			} catch (EntityExistsException duplicateEntry) {
				addActionError(getText("ReferNewClientSite.error.DupliateReferral"));
				return SUCCESS;
			}
			sendEmail();
		} else {
			if (status != null)
				newClientSite.setStatus(status);

			if (ClientSiteReferralStatus.ClosedContactedSuccessful == newClientSite.getStatus()) {
				prependToReferralNotes("Contacted Closed Successful with client site in PICS System.");
			} else if (ClientSiteReferralStatus.ClosedSuccessful == newClientSite.getStatus()) {
				prependToReferralNotes("Closed Successful with client site in PICS System.");
			}

			newClientSite = (ClientSiteReferral) basicDAO.save(newClientSite);
		}

		addActionMessage(getText("ReferNewClientSite.SuccessfullySaved"));
		return SUCCESS;
	}

	private void checkSourceFields() {
		if (newClientSite.getSource() == null || newClientSite.getSource().getId() == 0)
			addActionError(getText("ReferNewClientSite.error.FillSourceClientSiteName"));

		if (Strings.isEmpty(newClientSite.getSourceContact()))
			addActionError(getText("ReferNewClientSite.error.FillSourceContactName"));

		if (Strings.isEmpty(newClientSite.getSourcePhone()))
			addActionError(getText("ReferNewClientSite.error.FillSourcePhoneNumber"));

		if (Strings.isEmpty(newClientSite.getSourceEmail()) || !Strings.isValidEmail(newClientSite.getSourceEmail()))
			addActionError(getText("ReferNewClientSite.error.FillSourceValidEmail"));
	}

	private void checkReferenceFields() {
		if (Strings.isEmpty(newClientSite.getName()))
			addActionError(getText("ReferNewClientSite.error.FillClientSiteName"));

		if (Strings.isEmpty(newClientSite.getContact()))
			addActionError(getText("ReferNewClientSite.error.FillContactName"));

		if (Strings.isEmpty(newClientSite.getPhone()))
			addActionError(getText("ReferNewClientSite.error.FillPhoneNumber"));

		if (Strings.isEmpty(newClientSite.getEmail()) || !Strings.isValidEmail(newClientSite.getEmail()))
			addActionError(getText("ReferNewClientSite.error.FillValidEmail"));
	}

	public String contact() throws Exception {
		if (Strings.isEmpty(addToNotes) && !EMAIL.equals(contactType)) {
			addActionError(getText("ReferNewClientSite.error.EnterAdditionalNotes"));
			return SUCCESS;
		}

		String notes = "Contacted by " + contactType + ": " + addToNotes;

		if (EMAIL.equals(contactType)) {
			newClientSite.contactByEmail();
		} else
			newClientSite.contactByPhone();

		prependToReferralNotes(notes);
		newClientSite.setLastContactedBy(new User(permissions.getUserId()));
		newClientSite.setLastContactDate(new Date());

		basicDAO.save(newClientSite);

		return setUrlForRedirect("ReferNewClientSite.action?newClientSite=" + newClientSite.getId());
	}

	private void prependToReferralNotes(String note) {
		if (newClientSite != null && note != null)
			newClientSite.setNotes(maskDateFormat(new Date()) + " - " + permissions.getName() + " - " + note
					+ (newClientSite.getNotes() != null ? "\n\n" + newClientSite.getNotes() : ""));
	}

	private void sendEmail() {
		EmailBuilder emailBuilder = prepareEmailBuilder();
		try {
			EmailQueue q = emailBuilder.build();
			emailSenderSpring.send(q);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private EmailBuilder prepareEmailBuilder() {
		EmailBuilder email = new EmailBuilder();
		email.setToAddresses(EmailAddressUtils.PICS_MARKETING_EMAIL_ADDRESS);

		email.setFromAddress(EmailAddressUtils.PICS_INFO_EMAIL_ADDRESS);

		email.setTemplate(CLIENT_SITE_REFERRAL_NOTIFICATION_EMAIL);
		email.addToken("newClientSite", newClientSite);
		return email;
	}

	public ClientSiteReferral getNewClientSite() {
		return newClientSite;
	}

	public void setNewClientSite(ClientSiteReferral newClientSite) {
		this.newClientSite = newClientSite;
	}

	public String getAddToNotes() {
		return addToNotes;
	}

	public void setAddToNotes(String addToNotes) {
		this.addToNotes = addToNotes;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public ClientSiteReferralStatus getStatus() {
		return status;
	}

	public void setStatus(ClientSiteReferralStatus status) {
		this.status = status;
	}
}