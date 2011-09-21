package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.Locale;

import com.picsauditing.access.Anonymous;
import com.picsauditing.access.OpPerms;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class ContractorAgreement extends ContractorActionSupport {
	Locale request_locale = Locale.US;

	@Anonymous
	public String execute() throws Exception {
		if (permissions == null) {
			/**
			 * Block for passing in locale anonymously, for places like Email Subscriptions or Registration
			 */
			loadPermissions();
			User u = new User(User.SYSTEM);
			u.setLocale(request_locale);
			try {
				permissions.login(u);
			} catch (Exception e) {
			}
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
			accountDao.save(contractor);
		} else {
			addActionError("Only account Administrators, Billing, and Safety can accept this Contractor Agreement");
		}

		return SUCCESS;
	}

	public Locale getRequest_locale() {
		return request_locale;
	}

	public void setRequest_locale(Locale requestLocale) {
		request_locale = requestLocale;
	}

}
