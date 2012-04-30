package com.picsauditing.actions.audits;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
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

		List<AuditData> auditDataList = auditDataDao.findDataByCategory(audit.getId(), oshaType.categoryId);
		if (auditDataList == null) { 
			addActionError("Error locating data for verification.");
			return SUCCESS;
		}
		
		for (AuditData auditData : auditDataList) {
			auditData.setVerified(verify);
			auditData.setDateVerified(verifiedDate);
			auditData.setAuditor(auditor);
			String[] parameter = (String[]) ActionContext.getContext().getParameters().get("oshaQuestion_" + auditData.getQuestion().getId());
			if (parameter != null && parameter.length > 0) {
				auditData.setAnswer(parameter[0]);
			}
			auditDataDao.save(auditData);
		}

		auditPercentCalculator.percentCalculateComplete(audit, true);
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
