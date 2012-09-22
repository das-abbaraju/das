package com.picsauditing.report.models;

import com.picsauditing.access.Permissions
import com.picsauditing.report.tables.ContractorTable
import com.picsauditing.report.tables.ReportForeignKey
import com.picsauditing.report.tables.ReportTable

public class AccountContractorModel extends AbstractModel {
	public AccountContractorModel(Permissions permissions) {
		super(permissions)
		fromTable = new ContractorTable()
		
		def map = [
			alias: "Contractor",
			joins: [
				[key: "Account", joins: ["Contact", "Naics"] ],
				[key: "CSR", alias: "AccountCSR", required: false]
			]
		]
		ReportJoin join = parse(map)
		System.out.println(join);

//		from "Contractor" join (
//			{
//				"Account" join (
//					{
//						"Contact"
//					}
//				)
//			}
//		)
		
		from("Contractor").join ( 
			{ReportTable it -> 
				it.to("Account").join (
					{ReportTable it2 -> 
						it2.to("Contact")
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
	
	ReportJoin parse(Map joinDef) {
		ReportJoin join = new ReportJoin();
		println "Map.alias = "+map.alias
		return join
	}
}
