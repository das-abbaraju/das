package com.picsauditing.actions.audits;

import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

/**
 * This class isn't being used yet, but it's designed to view audit cat/section/data
 * @author Trevor
 *
 */
public class AuditDataView extends AuditActionSupport {
	int categoryID;
	
	public AuditDataView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		return SUCCESS;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}
}
