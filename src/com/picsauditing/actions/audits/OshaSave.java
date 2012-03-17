package com.picsauditing.actions.audits;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;

@SuppressWarnings("serial")
public class OshaSave extends PicsActionSupport {
	
	private OshaType oshaType;
	private ContractorAudit audit;
	private boolean verify;

	@Autowired
	private AuditDataDAO auditDataDao;
	
	public String execute() {
		OshaAudit oshaAudit = new OshaAudit(audit);
		for (AuditData auditData: oshaAudit.getAllQuestionsInOshaType(oshaType)) {
			auditData.setVerified(verify);
			if (verify) {
				auditData.setDateVerified(new Date());
				auditData.setAuditor(getUser());
			}
			else {
				auditData.setDateVerified(null);
				auditData.setAuditor(null);
			}
			auditDataDao.save(auditData);
		}
		return SUCCESS;
	}

	public OshaType getOshaType() {
		return oshaType;
	}

	public void setOshaType(OshaType oshaType) {
		this.oshaType = oshaType;
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}
}
