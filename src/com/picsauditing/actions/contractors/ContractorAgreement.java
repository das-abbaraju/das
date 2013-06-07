package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.Locale;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.model.i18n.LanguageModel;

@SuppressWarnings("serial")
public class ContractorAgreement extends ContractorActionSupport {

	private Locale request_locale;

	@Anonymous
	public String execute() throws Exception {
		if (permissions == null) {
			loadPermissions();
		}

		if (request_locale != null) {
			permissions.setLocale(request_locale);
		} else if (request_locale == null && permissions.getLocale() == null) {
			// this case should never happen, but leaving it just in case
			permissions.setLocale(LanguageModel.ENGLISH);
		}

		return SUCCESS;
	}

	public String agree() {
		if (!permissions.isAdmin()
				&& (permissions.hasPermission(OpPerms.ContractorAdmin)
						|| permissions.hasPermission(OpPerms.ContractorBilling) || permissions
							.hasPermission(OpPerms.ContractorSafety))) {
			contractor.setAgreementDate(new Date());
			contractor.setAgreedBy(getUser());
			contractorAccountDao.save(contractor);
		} else {
			addActionError("Only account Administrators, Billing, and Safety can accept this Contractor Agreement");
		}

		return SUCCESS;
	}

	@Anonymous
	public String print() {
		return "print";
	}

	public Locale getRequest_locale() {
		return request_locale;
	}

	public void setRequest_locale(Locale requestLocale) {
		request_locale = requestLocale;
	}

}
