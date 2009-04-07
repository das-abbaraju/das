package com.picsauditing.actions.audits;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
public class ConAuditMaintain extends AuditActionSupport implements Preparable {

	public ConAuditMaintain(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.AuditEdit);

		if ("Save".equals(button)) {
			auditDao.clear();
			if (conAudit.getAuditor().getId() == 0)
				conAudit.setAuditor(null);
			auditDao.save(conAudit);
			findConAudit();
			addActionMessage("Successfully saved data");
		}
		if ("Delete".equals(button)) {
			auditDao.clear();
			auditDao.remove(auditID, getFtpDir());
			return "AuditList";
		}

		return SUCCESS;
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;

		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("auditID");

		if (ids != null && ids.length > 0) {
			auditID = new Integer(ids[0]).intValue();
			findConAudit();
		}
	}

}
