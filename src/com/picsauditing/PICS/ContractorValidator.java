package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

public class ContractorValidator {
	public final int MIN_PASSWORD_LENGTH = 5;
	@Autowired
	protected ContractorAccountDAO contractorAccountDao;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected ContractorAuditDAO contractorAuditDao;
	@Autowired
	protected AuditDataDAO auditDataDao;
	@Autowired
	protected AuditQuestionDAO auditQuestionDao;
    @Autowired
    protected VATValidator vatValidator;

	private I18nCache i18nCache = I18nCache.getInstance();

	public static Locale getLocaleStatic() {
		try {
			return (Locale) ActionContext.getContext().get(ActionContext.LOCALE);
		} catch (Exception defaultToEnglish) {
			return Locale.ENGLISH;
		}
	}

	private String getText(String aTextName) {
		return i18nCache.getText(aTextName, getLocaleStatic());
	}

	public String getTextParameterized(String aTextName, Object... args) {
		return i18nCache.getText(aTextName, getLocaleStatic(), Arrays.asList(args));
	}

	public Vector<String> validateContractor(ContractorAccount contractor) {
		Vector<String> errorMessages = new Vector<String>();
		if (contractor.getType() == null) {
			errorMessages.addElement(getText("ContractorValidator.error.NoAccountType"));
			return errorMessages;
		}

		// Company Name
		if (Strings.isEmpty(contractor.getName()))
			errorMessages.addElement(getText("ContractorValidator.error.NoCompanyName"));
		else if (contractor.getName().length() < 3)
			errorMessages.addElement(getText("ContractorValidator.error.CompanyNameNotLongEnoough"));

		if (Strings.isEmpty(contractor.getAddress()))
			errorMessages.addElement(getText("ContractorValidator.error.NoAddress"));
		if (Strings.isEmpty(contractor.getCity()))
			errorMessages.addElement(getText("ContractorValidator.error.NoAdCity"));
		if (contractor.getCountry() == null || Strings.isEmpty(contractor.getCountry().getIsoCode()))
			errorMessages.addElement(getText("ContractorValidator.error.NoCountry"));
		if (contractor.getCountry() != null && contractor.getCountry().hasCountrySubdivisions()) {
			if (contractor.getCountrySubdivision() == null || Strings.isEmpty(contractor.getCountrySubdivision().getIsoCode())) {
				errorMessages.addElement(getText("ContractorValidator.error.NoCountrySubdivision"));
			}
		}

		if (Strings.isEmpty(contractor.getPhone()))
			errorMessages.addElement(getText("ContractorValidator.error.NoPhone"));

		if (contractor.getCountry() != null && !contractor.getCountry().isUAE() && Strings.isEmpty(contractor.getZip()))
			errorMessages.addElement(getText("ContractorValidator.error.PleaseFillInZip"));

		// Onsite / Offsite / Material Supplier
		if (contractor.getAccountTypes().isEmpty())
			errorMessages.addElement(getText("ContractorValidator.error.NoServiceSelection"));

        if (!Strings.isEmpty(contractor.getVatId())) {
            try {
                vatValidator.validated(contractor.getVatId());
            } catch (Exception e) {
                errorMessages.addElement(getText("ContractorValidator.error.InvalidVAT"));
            }
        }

		return errorMessages;
	}

	public Vector<String> validateUser(String password1, String password2, User user) {
		Vector<String> errorMessages = new Vector<String>();

		// Username
		String username = user.getUsername().trim();
		if (Strings.isEmpty(username))
			errorMessages.addElement(getText("User.username.error.Empty"));
		else if (username.length() < 3)
			errorMessages.addElement(getText("User.username.error.Short"));
		else if (username.length() > 100)
			errorMessages.addElement(getText("User.username.error.Long"));
		else if (username.contains(" "))
			errorMessages.addElement(getText("User.username.error.Space"));
		else if (!username.matches("^[a-zA-Z0-9+._@-]{3,50}$"))
			errorMessages.addElement(getText("User.username.error.Special"));

		if (!verifyUsername(user))
			errorMessages.addElement(getText("ContractorValidator.error.DuplicateUserName"));

		if (Strings.isEmpty(user.getName()))
			errorMessages.addElement(getText("ContractorValidator.error.NoPrimaryContact"));
		if (!Strings.isValidEmail(user.getEmail()))
			errorMessages.addElement(getText("ContractorValidator.error.NoEmail"));

		// Passwords
		if (!Strings.isEmpty(password2)) {
			// They are trying to set/reset the password

			if (!password1.equals(password2))
				errorMessages.addElement(getText("ContractorValidator.error.PasswordsDontMatch"));

			if (password1.length() < MIN_PASSWORD_LENGTH)
				errorMessages.addElement(getTextParameterized("ContractorValidator.error.InvalidPasswordLength",
						MIN_PASSWORD_LENGTH));
			if (password1.equalsIgnoreCase(user.getUsername()))
				errorMessages.addElement(getText("ContractorValidator.error.InvalidPassword2"));
			if (password1.equalsIgnoreCase("password"))
				errorMessages.addElement(getText("ContractorValidator.error.InvalidPassword1"));
			// TODO - Remove Side-effect
			if (errorMessages.size() == 0) {
				user.setPassword(password1);
				user.setPasswordChanged(new Date());
			}
		}

		return errorMessages;
	}

	public boolean verifyUsername(User user) {
		User foundUser = userDAO.findName(user.getUsername());
		if (foundUser == null)
			return true;
		if (user.getId() > 0) {
			user = userDAO.find(user.getId());
			if (foundUser.equals(user))
				return true;
		}
		return false;
	}

	public Vector<String> verifyTaxID(ContractorAccount contractorAccount) {
		Vector<String> errorMessages = new Vector<String>();

		String taxId = contractorAccount.getTaxId();
		String country = contractorAccount.getCountry().getIsoCode();

		if ("AE".equals(country))
			return errorMessages;

		if (!Strings.isEmpty(taxId) && !Strings.isEmpty(country)) {
			if ("CA".equals(country) && taxId.length() != 15) {
				errorMessages.add(getTextParameterized(
						"ContractorValidator.error.InvalidBusinessNumber",
						getText("PicsCustomerServicePhone")));
				return errorMessages;
			} else if (!"CA".equals(country) && taxId.length() != 9) {
				errorMessages.add(getText("ContractorValidator.error.InvalidTaxId"));
				return errorMessages;
			}

			ContractorAccount con = contractorAccountDao.findTaxID(taxId.substring(0, 9), country);
			if (con != null && !con.equals(contractorAccount)) {
				if (con.getCountry().isUS())
					errorMessages.add(getTextParameterized(
							"ContractorValidator.error.DuplicateTaxId",
							getText("PicsCustomerServicePhone")));
			}
		}

		return errorMessages;
	}

	public boolean verifyName(ContractorAccount contractorAccount) {
		ContractorAccount cAccount = contractorAccountDao.findConID(contractorAccount.getName());
		if (cAccount == null || cAccount.equals(contractorAccount))
			return true;

		return false;
	}

	public void setOfficeLocationInPqfBasedOffOfAddress(
			ContractorAccount contractor) {
		
		if (contractor == null)
			return;
		
		List<String> uniqueCodes = new ArrayList<String>();
		uniqueCodes.add(contractor.getCountrySubdivision().toString());

		List<AuditQuestion> officeLocationResultSet = auditQuestionDao
				.findQuestionsByUniqueCodes(uniqueCodes);
		if (officeLocationResultSet.isEmpty())
			return;

		AuditQuestion officeLocationQuestion = officeLocationResultSet.get(0);
		
		changeAnswerInAuditData(officeLocationQuestion, contractor);
	}

	private void changeAnswerInAuditData(AuditQuestion officeLocationQuestion, ContractorAccount contractor) {
		ContractorAudit contractorsPqf = null;
		
		for (ContractorAudit contractorAudit : contractorAuditDao
				.findByContractor(contractor.getId())) {
			if (contractorAudit.getAuditType().isPqf()) {
				contractorsPqf = contractorAudit;
				break;
			}
		}

		if (contractorsPqf != null) {
			AuditData locationData = auditDataDao.findAnswerByAuditQuestion(
					contractorsPqf.getId(), officeLocationQuestion.getId());
			if (locationData == null) {
				locationData = new AuditData();
				locationData.setAuditColumns(contractor.getPrimaryContact());
				locationData.setId(0);
				locationData.setAudit(contractorsPqf);
				locationData.setQuestion(officeLocationQuestion);
			}
			locationData.setAnswer("YesWithOffice");
			auditDataDao.save(locationData);
		}
	}
}
