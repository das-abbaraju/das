package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

public class ContractorRegistrationRequestValidator {
	private I18nCache i18nCache = I18nCache.getInstance();
	private Locale locale = TranslationActionSupport.getLocaleStatic();

	private ContractorRegistrationRequest request;
	private List<String> errors = new ArrayList<String>();

	public ContractorRegistrationRequestValidator(ContractorRegistrationRequest request) {
		this.request = request;
	}

	public List<String> validate() {
		checkContactFields();
		checkOperatorSpecifiedFields();
		checkStatusRequirements();

		return errors;
	}

	private void checkContactFields() {
		if (Strings.isEmpty(request.getName())) {
			errors.add(i18nCache.getText("Requestrequest.error.FillContractorName", locale));
		}

		if (Strings.isEmpty(request.getContact())) {
			errors.add(i18nCache.getText("Requestrequest.error.FillContactName", locale));
		}

		if (request.getCountry() == null) {
			errors.add(i18nCache.getText("Requestrequest.error.SelectCountry", locale));
		} else if (request.getCountry().getIsoCode().equals("US") || request.getCountry().getIsoCode().equals("CA")) {
			if (request.getCountrySubdivision() == null
					|| Strings.isEmpty(request.getCountrySubdivision().getIsoCode()))
				errors.add(i18nCache.getText("Requestrequest.error.SelectCountrySubdivision", locale));
		}

		if (Strings.isEmpty(request.getPhone())) {
			errors.add(i18nCache.getText("Requestrequest.error.FillPhoneNumber", locale));
		}

		if (Strings.isEmpty(request.getEmail()) || !EmailAddressUtils.isValidEmail(request.getEmail())) {
			errors.add(i18nCache.getText("Requestrequest.error.FillValidEmail", locale));
		}
	}

	private void checkOperatorSpecifiedFields() {
		if (request.getRequestedBy() == null) {
			errors.add(i18nCache.getText("Requestrequest.error.SelectRequestedByAccount", locale));
		}

		if (request.getRequestedByUser() == null && Strings.isEmpty(request.getRequestedByUserOther())) {
			errors.add(i18nCache.getText("Requestrequest.error.SelectRequestedUser", locale));
		}

		if (request.getDeadline() == null) {
			errors.add(i18nCache.getText("Requestrequest.error.SelectDeadline", locale));
		}

		if (Strings.isEmpty(request.getReasonForRegistration())) {
			errors.add(i18nCache.getText("Requestrequest.error.EnterRegistrationReason", locale));
		}
	}

	private void checkStatusRequirements() {
		if (ContractorRegistrationRequestStatus.Hold.equals(request.getStatus()) && request.getHoldDate() == null) {
			errors.add(i18nCache.getText("RequestNewContractor.error.EnterHoldDate", locale));
		}

		if ((ContractorRegistrationRequestStatus.ClosedContactedSuccessful.equals(request.getStatus()) || ContractorRegistrationRequestStatus.ClosedSuccessful
				.equals(request.getStatus())) && request.getContractor() == null) {
			errors.add(i18nCache.getText("RequestNewContractor.error.PICSContractorNotFound", locale));
		}

		if (ContractorRegistrationRequestStatus.ClosedUnsuccessful.equals(request.getStatus())
				&& Strings.isEmpty(request.getReasonForDecline())) {
			errors.add(i18nCache.getText("RequestNewContractor.error.EnterReasonDeclined", locale));
		}

		if (request.getId() > 0 && request.getStatus() == null) {
			errors.add(i18nCache.getText("RequestNewContractor.error.StatusMissing", locale));
		}
	}
}
