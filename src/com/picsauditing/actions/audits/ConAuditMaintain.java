package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.ContractorAuditOperatorWorkflowDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class ConAuditMaintain extends AuditActionSupport implements Preparable {

	protected List<ContractorAuditOperator> caosSave = new ArrayList<ContractorAuditOperator>();

	public ConAuditMaintain(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao,
			ContractorAuditOperatorDAO caoDAO, ContractorAuditOperatorWorkflowDAO caowDAO,
			AuditCategoryRuleCache auditCategoryRuleCache) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("auditID");

		if (ids != null && ids.length > 0) {
			auditID = Integer.parseInt(ids[0]);
			findConAudit();
		}

		/*
		 * This page defaults to systemEdit = true. If something is trying to
		 * set this value, use that instead. This is for the cao editing ability
		 * on the main audit page.
		 */
		String[] systemEdits = (String[]) ActionContext.getContext().getParameters().get("systemEdit");
		if (systemEdits != null && systemEdits.length > 0) {
			systemEdit = Boolean.parseBoolean(systemEdits[0]);
		} else {
			systemEdit = true;
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if ("caoSave".equals(button)) {
				for (ContractorAuditOperator cao : caosSave) {
					if (cao != null) {
						ContractorAuditOperator toSave = caoDAO.find(cao.getId());
						if (toSave != null) {
							toSave.setVisible(cao.isVisible());
							AuditStatus prevStatus = toSave.getStatus();
							AuditStatus newStatus = cao.getStatus();
							auditSetExpiresDate(toSave, newStatus);
							toSave.changeStatus(newStatus, permissions);
							
							setCaoUpdatedNote(prevStatus, toSave);

							caoDAO.save(toSave);
						}
					}
				}
				return SUCCESS;
			}

			// Check if they have the AuditEdit permission here
			permissions.tryPermission(OpPerms.AuditEdit);

			if ("Save".equals(button)) {
				auditDao.clear();
				if (conAudit.getAuditor().getId() == 0)
					conAudit.setAuditor(null);
				conAudit.setAuditColumns(permissions);
				auditDao.save(conAudit);
				findConAudit();
				addNote(conAudit.getContractorAccount(), "Modified " + conAudit.getAuditType().getName()
						+ " using System Edit", NoteCategory.Audits, LowMedHigh.Low, false, Account.PicsID,
						this.getUser());
				addActionMessage("Successfully saved data");
			}
			if ("Delete".equals(button)) {
				for (Iterator<ContractorAuditOperator> caoIT = conAudit.getOperators().iterator(); caoIT.hasNext();) {
					ContractorAuditOperator cao = caoIT.next();
					List<ContractorAuditOperatorWorkflow> caowList = caowDAO.findByCaoID(cao.getId());
					for (Iterator<ContractorAuditOperatorWorkflow> it = caowList.iterator(); it.hasNext();) {
						ContractorAuditOperatorWorkflow t = it.next();
						it.remove();
						caowDAO.remove(t);
					}

					for (Iterator<ContractorAuditOperatorPermission> it = cao.getCaoPermissions().iterator(); it
							.hasNext();) {
						ContractorAuditOperatorPermission t = it.next();
						it.remove();
						caoDAO.remove(t);
					}
					caoIT.remove();
					caoDAO.remove(cao);
				}
				auditDao.clear();
				auditDao.remove(auditID, getFtpDir());
				addNote(conAudit.getContractorAccount(), "Deleted " + conAudit.getAuditType().getAuditName(),
						NoteCategory.Audits, LowMedHigh.Low, false, Account.PicsID, this.getUser());
				if (conAudit.getAuditType().getClassType().isPolicy())
					return "PolicyList";
				return "AuditList";
			}
		}

		return SUCCESS;
	}

	public List<ContractorAuditOperator> getCaosSave() {
		return caosSave;
	}

	public void setCaosSave(List<ContractorAuditOperator> caosSave) {
		this.caosSave = caosSave;
	}
	
}