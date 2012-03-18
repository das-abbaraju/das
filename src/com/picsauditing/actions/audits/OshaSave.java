package com.picsauditing.actions.audits;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.auditBuilder.AuditPercentCalculator;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class OshaSave extends PicsActionSupport {
	
	private OshaType oshaType;
	private ContractorAudit audit;
	private boolean verify;
	private OshaAudit oshaAudit;
	private Date verifiedDate = null;
	private User auditor = null;
	private String comment;

	@Autowired
	private AuditDataDAO auditDataDao;
	@Autowired
	private AuditPercentCalculator auditPercentCalculator;
	@Autowired
	private ContractorAuditDAO contractorAuditDao;
	
	public String execute() {
		oshaAudit = new OshaAudit(audit);
		
		if (verify) {
			verifiedDate = new Date();
			auditor = getUser();
		}
		for (AuditData auditData: oshaAudit.getAllQuestionsInOshaType(oshaType)) {
			auditData.setVerified(verify);
			auditData.setDateVerified(verifiedDate);
			auditData.setAuditor(auditor);
			auditDataDao.save(auditData);
		}
		auditPercentCalculator.percentCalculateComplete(audit);
		contractorAuditDao.save(audit);
		return SUCCESS;
	}
	
	public String stampOshaComment() {
		oshaAudit = new OshaAudit(audit);
		auditDataDao.save(oshaAudit.stampOshaComment(oshaType, comment));
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

	public OshaAudit getOshaAudit() {
		return oshaAudit;
	}

	public Date getVerifiedDate() {
		return verifiedDate;
	}

	public User getAuditor() {
		return auditor;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
