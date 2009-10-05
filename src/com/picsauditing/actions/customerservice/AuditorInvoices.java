package com.picsauditing.actions.customerservice;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class AuditorInvoices extends PicsActionSupport {
	@Override
	public String execute() throws Exception {
		loadPermissions();
		int auditorID = 0;

		if (permissions.hasPermission(OpPerms.AuditorPayments)) {
			// Jesse can see everyone's Audits and Payments
			auditorID = 0;
		} else if (permissions.hasGroup(User.INDEPENDENT_CONTRACTOR)) {
			auditorID = permissions.getUserId();
		}
		return SUCCESS;
	}
}
