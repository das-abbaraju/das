package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.ContractorTable
import com.picsauditing.report.tables.ReportForeignKey

public class AccountContractorModel extends AbstractModel {

	public AccountContractorModel(Permissions permissions) {
		super(permissions)
		fromTable = new ContractorTable()

		from "Contractor" join ( 
			{
				"Account" join (
					{
						"Contact"
					}                 
				)
			}
		)

		availableFields = fromTable.getAvailableFields(permissions);
		// Like this
		//	conAudit ContractorAuditTable
		//		contractor Level.Required
		//			account
		//				accountContact
		//				accountNaics
		//		auditType

		// TODO adjust these columns
		// hideAccountID();
		// rootTable.removeField("accountName");
		// rootTable.removeField("accountType");
	}
}
