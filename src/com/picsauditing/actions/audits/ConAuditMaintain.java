package com.picsauditing.actions.audits;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
public class ConAuditMaintain extends AuditActionSupport implements Preparable {

	protected OperatorAccountDAO opDao = null;
	protected AuditTypeDAO auditTypeDao = null;

	public ConAuditMaintain(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, OperatorAccountDAO opDao,
			AuditTypeDAO auditTypeDao) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.opDao = opDao;
		this.auditTypeDao = auditTypeDao;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.AuditEdit);

		if (button != null) {
			if (button.equals("Save")) {

				if (conAudit.getAuditor().getId() == 0)
					conAudit.setAuditor(null);
				auditDao.save(conAudit);
				findConAudit();
				addActionMessage("Successfully saved data");
			}
			if(button.equals("Delete")) {
				auditDao.clear();
				auditDao.remove(conAudit, getFtpDir());
			}
		}

		return SUCCESS;
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("auditID");

		if (ids != null && ids.length > 0) {
			auditID = new Integer(ids[0]).intValue();
			findConAudit();
			auditDao.clear();
		}
	}

}
