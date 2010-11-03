package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
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

	protected ContractorAuditOperatorDAO caoDAO;
	protected List<ContractorAuditOperator> caosSave = new ArrayList<ContractorAuditOperator>();
	
	public ConAuditMaintain(ContractorAccountDAO accountDao,
			ContractorAuditDAO auditDao, CertificateDAO certificateDao, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, ContractorAuditOperatorDAO caoDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao, certificateDao);
		this.caoDAO = caoDAO;
	}

	public void prepare() throws Exception {
		if (!forceLogin())
			return;

		String[] ids = (String[]) ActionContext.getContext().getParameters()
				.get("auditID");

		if (ids != null && ids.length > 0) {
			auditID = Integer.parseInt(ids[0]);
			findConAudit();
		}
	}

	public String execute() throws Exception {
		systemEdit = true;
		if (!forceLogin())
			return LOGIN;
		
		permissions.tryPermission(OpPerms.AuditEdit);
		
		if ("caoSave".equals(button)) {
			for (ContractorAuditOperator cao : caosSave) {
				if(cao!=null){
					ContractorAuditOperator toSave = caoDAO.find(cao.getId());
					if (toSave != null) {
						toSave.setVisible(cao.isVisible());
						toSave.changeStatus(cao.getStatus(), permissions);
						caoDAO.save(toSave);
					}
				}
			}
			return SUCCESS;
		}
		if ("Save".equals(button)) {
			auditDao.clear();
			if (conAudit.getAuditor().getId() == 0)
				conAudit.setAuditor(null);
			conAudit.setAuditColumns(permissions);
			auditDao.save(conAudit);
			findConAudit();
			addNote(conAudit.getContractorAccount(), "Modified "
					+ conAudit.getAuditType().getAuditName()
					+ " using System Edit", NoteCategory.Audits,
					LowMedHigh.Low, false, Account.PicsID, this.getUser());
			addActionMessage("Successfully saved data");
		}
		if ("Delete".equals(button)) {
			auditDao.clear();
			for(ContractorAuditOperator cao : conAudit.getOperators()){
				caoDAO.remove(cao);
			}
			auditDao.remove(auditID, getFtpDir());
			addNote(conAudit.getContractorAccount(), "Deleted "
					+ conAudit.getAuditType().getAuditName(),
					NoteCategory.Audits, LowMedHigh.Low, false, Account.PicsID,
					this.getUser());
			if (conAudit.getAuditType().getClassType().isPolicy())
				return "PolicyList";
			return "AuditList";
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
