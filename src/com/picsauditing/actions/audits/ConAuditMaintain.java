package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
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
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.WorkflowStep;

/**
 * Class used to edit a ContractorAudit record with virtually no restrictions
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class ConAuditMaintain extends AuditActionSupport implements Preparable {

	protected ContractorAuditOperatorDAO caoDAO;
	protected ContractorAuditOperatorWorkflowDAO caowDAO;
	protected NoteDAO noteDAO;
	protected List<ContractorAuditOperator> caosSave = new ArrayList<ContractorAuditOperator>();

	public ConAuditMaintain(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			CertificateDAO certificateDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao,
			ContractorAuditOperatorDAO caoDAO, ContractorAuditOperatorWorkflowDAO caowDAO,
			AuditCategoryRuleCache auditCategoryRuleCache, NoteDAO noteDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao, auditCategoryRuleCache);
		this.caoDAO = caoDAO;
		this.caowDAO = caowDAO;
		this.noteDAO = noteDAO;
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("auditID");

		if (ids != null && ids.length > 0) {
			auditID = Integer.parseInt(ids[0]);
			findConAudit();
		}
	}

	public String execute() throws Exception {
		systemEdit = true;

		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if ("caoSave".equals(button)) {
				for (ContractorAuditOperator cao : caosSave) {
					if (cao != null) {
						ContractorAuditOperator toSave = caoDAO.find(cao.getId());
						if (toSave != null) {
							toSave.setVisible(cao.isVisible());

							// Stamping cao workflow automatically...
							ContractorAuditOperatorWorkflow caoW = new ContractorAuditOperatorWorkflow();
							Note newNote = new Note();
							newNote.setAccount(toSave.getAudit().getContractorAccount());
							newNote.setAuditColumns(permissions);
							newNote.setSummary("Changed Status for "+ toSave.getAudit().getAuditType().getAuditName() +"("+toSave.getAudit().getId()+") from " + toSave.getStatus() + " to "
									+ cao.getStatus() + " for " + toSave.getOperator().getName());
							newNote.setNoteCategory(NoteCategory.Audits);
							newNote.setViewableBy(toSave.getOperator());
							noteDAO.save(newNote);
							caoW.setNotes("Changed Status from " + toSave.getStatus() + " to " + cao.getStatus()
									+ " by " + permissions.getUserId() + " on " + new Date());
							caoW.setCao(toSave);
							caoW.setAuditColumns(permissions);
							caoW.setPreviousStatus(toSave.getStatus());
							caoW.setStatus(cao.getStatus());
							caoDAO.save(caoW);

							toSave.changeStatus(cao.getStatus(), permissions);
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
				addNote(conAudit.getContractorAccount(), "Modified " + conAudit.getAuditType().getAuditName()
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

	private boolean requiresNote(AuditStatus o, AuditStatus n, ContractorAudit c) {
		for (WorkflowStep s : c.getAuditType().getWorkFlow().getSteps()) {
			if (((s.getOldStatus() != null && s.getOldStatus().equals(o)) || o == null) && s.getNewStatus().equals(n))
				return s.isNoteRequired();
		}

		return false;
	}
}