package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.picsauditing.actions.TranslationActionSupport;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationRequestStatus;
import com.picsauditing.service.i18n.TranslationService;
import com.picsauditing.service.i18n.TranslationServiceFactory;
import com.picsauditing.util.EmailAddressUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("deprecation")
public class ContractorRegistrationRequestValidator {
	private TranslationService translationService = TranslationServiceFactory.getTranslationService();
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
			errors.add(translationService.getText("Requestrequest.error.FillContractorName", locale));
		}

		if (Strings.isEmpty(request.getContact())) {
			errors.add(translationService.getText("Requestrequest.error.FillContactName", locale));
		}

		if (request.getCountry() == null) {
			errors.add(translationService.getText("Requestrequest.error.SelectCountry", locale));
		} else if (request.getCountry().getIsoCode().equals("US") || request.getCountry().getIsoCode().equals("CA")) {
			if (request.getCountrySubdivision() == null
					|| Strings.isEmpty(request.getCountrySubdivision().getIsoCode())) {
				errors.add(translationService.getText("Requestrequest.error.SelectCountrySubdivision", locale));
			}
		}

		if (Strings.isEmpty(request.getPhone())) {
			errors.add(translationService.getText("Requestrequest.error.FillPhoneNumber", locale));
		}

		if (Strings.isEmpty(request.getEmail()) || !EmailAddressUtils.isValidEmail(request.getEmail())) {
			errors.add(translationService.getText("Requestrequest.error.FillValidEmail", locale));
		}
	}

	private void checkOperatorSpecifiedFields() {
		if (request.getRequestedBy() == null) {
			errors.add(translationService.getText("Requestrequest.error.SelectRequestedByAccount", locale));
		}

		if (request.getRequestedByUser() == null && Strings.isEmpty(request.getRequestedByUserOther())) {
			errors.add(translationService.getText("Requestrequest.error.SelectRequestedUser", locale));
		}

		if (request.getDeadline() == null) {
			errors.add(translationService.getText("Requestrequest.error.SelectDeadline", locale));
		}

		if (Strings.isEmpty(request.getReasonForRegistration())) {
			errors.add(translationService.getText("Requestrequest.error.EnterRegistrationReason", locale));
		}
	}

	private void checkStatusRequirements() {
		if (ContractorRegistrationRequestStatus.Hold.equals(request.getStatus()) && request.getHoldDate() == null) {
			errors.add(translationService.getText("RequestNewContractor.error.EnterHoldDate", locale));
		}

		if ((ContractorRegistrationRequestStatus.ClosedContactedSuccessful.equals(request.getStatus()) || ContractorRegistrationRequestStatus.ClosedSuccessful
				.equals(request.getStatus())) && request.getContractor() == null) {
			errors.add(translationService.getText("RequestNewContractor.error.PICSContractorNotFound", locale));
		}

		if (ContractorRegistrationRequestStatus.ClosedUnsuccessful.equals(request.getStatus())
				&& Strings.isEmpty(request.getReasonForDecline())) {
			errors.add(translationService.getText("RequestNewContractor.error.EnterReasonDeclined", locale));
		}

		if (request.getId() > 0 && request.getStatus() == null) {
			errors.add(translationService.getText("RequestNewContractor.error.StatusMissing", locale));
		}
	}
}
