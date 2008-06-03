package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NcmsCategoryDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.mail.EmailAuditBean;
import com.picsauditing.mail.EmailTemplates;

public class ContractorAuditAction extends AuditActionSupport {
	protected AuditStatus auditStatus;
	protected List<AuditCatData> categories;
	protected AuditCategoryDataDAO catDataDao;
	protected EmailAuditBean mailer;
	protected FlagCalculator2 flagCalculator;

	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, EmailAuditBean emailAuditBean, FlagCalculator2 flagCalculator2) {
		super(accountDao, auditDao, auditDataDao);
		this.catDataDao = catDataDao;
		this.mailer = emailAuditBean;
		this.flagCalculator = flagCalculator2;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();
		
		if (auditStatus != null && !conAudit.getAuditStatus().equals(auditStatus)) {
			if (!conAudit.getAuditType().isHasRequirements() && auditStatus.equals(AuditStatus.Submitted)) {
				// This audit should skip directly to Active when Submitted
				auditStatus = AuditStatus.Active;
				conAudit.setCompletedDate(new Date());
			}
			if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
				conAudit.setCompletedDate(new Date());
				
				// Send email to contractors telling them the Audit was submitted
				mailer.setPermissions(permissions);
				if (conAudit.getAuditType().getAuditTypeID() == AuditType.DESKTOP) {
					mailer.sendMessage(EmailTemplates.desktopsubmit, conAudit);
				}
				if (conAudit.getAuditType().getAuditTypeID() == AuditType.DA) {
					mailer.sendMessage(EmailTemplates.dasubmit, conAudit);
				}
			}
			if (conAudit.getAuditStatus().equals(AuditStatus.Active)) {
				conAudit.setClosedDate(new Date());
			}
			
			if (conAudit.getExpiresDate() == null && conAudit.getCompletedDate() != null) {
				Date dateToExpire = conAudit.getAuditType().getDateToExpire();
				Calendar cal = Calendar.getInstance();
				if (dateToExpire == null) {
					cal.setTime(conAudit.getCompletedDate());
					cal.add(Calendar.MONTH, conAudit.getAuditType().getMonthsToExpire());
				} else {
					cal.setTime(dateToExpire);
					cal.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
					cal.before(new Date());
					cal.add(Calendar.YEAR, 1);
				}
				conAudit.setExpiresDate(cal.getTime());
			}
			
			// Save the audit status
			conAudit.setAuditStatus(auditStatus);
			auditDao.save(conAudit);
			
			flagCalculator.runByContractor(conAudit.getContractorAccount().getId());
		}
		
		if (this.conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS)
			return "NCMS";

		return SUCCESS;
	}
	
	/**
	 * Can the current user submit this audit in its current state?
	 * @return
	 */
	public boolean isCanSubmit() {
		if (!isCanEdit())
			return false;
		if (conAudit.getPercentComplete() < 100)
			return false;
		if (conAudit.getAuditStatus().equals(AuditStatus.Pending))
			return true;
		return false;
	}
	
	
	/**
	 * Can the current user submit this audit in its current state?
	 * @return
	 */
	public boolean isCanClose() {
		if (!isCanEdit())
			return false;
		if (conAudit.getPercentVerified() < 100)
			return false;
		if (conAudit.getAuditStatus().equals(AuditStatus.Submitted))
			return true;
		return false;
	}

	public List<AuditCatData> getCategories() {
		if (conAudit.getAuditStatus().equals(AuditStatus.Exempt))
			return null;

		if (categories == null) {
			categories = catDataDao.findByAudit(conAudit, permissions);
		}
		return categories;
	}

	public List<NcmsCategory> getNcmsCategories() {
		try {
			NcmsCategoryDAO dao = new NcmsCategoryDAO();
			return dao.findCategories(this.id);
		} catch (Exception e) {
			List<NcmsCategory> error = new ArrayList<NcmsCategory>();
			NcmsCategory cat = new NcmsCategory();
			cat.setName("Error retrieving list");
			error.add(cat);
			return error;
		}
	}

	public String getCatUrl() {
		if (!isCanEdit())
			return "pqf_view.jsp";
		
		if (conAudit.getAuditStatus().equals(AuditStatus.Pending))
			return "pqf_edit.jsp";
		
		if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)) {
			if (isCanVerify())
				return "pqf_edit.jsp";
			else
				return "pqf_view.jsp";
		}
		
		// Active/Exempt/Expired
		return "pqf_view.jsp";
	}

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

}
