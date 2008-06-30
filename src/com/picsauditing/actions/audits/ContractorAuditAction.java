package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NcmsCategoryDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailAuditBean;
import com.picsauditing.mail.EmailTemplates;

/**
 * Used by Audit.action to show a list of categories for a given audit. 
 * Also allows users to change the status of an audit.
 * @author Trevor
 *
 */
public class ContractorAuditAction extends AuditActionSupport {
	protected AuditStatus auditStatus;
	protected EmailAuditBean mailer;
	protected FlagCalculator2 flagCalculator;
	protected AuditPercentCalculator auditPercentCalculator;

	private boolean isCanApply = false;
	private int applyCategoryID = 0;
	private int removeCategoryID = 0;
	
	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, 
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, EmailAuditBean emailAuditBean, 
			FlagCalculator2 flagCalculator2, AuditPercentCalculator auditPercentCalculator) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.mailer = emailAuditBean;
		this.flagCalculator = flagCalculator2;
		this.auditPercentCalculator = auditPercentCalculator;
	}
	
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();
		
		catDataDao.fillAuditCategories(conAudit);
		
		if (conAudit.getAuditType().isDynamicCategories() && permissions.isPicsEmployee()) {
			isCanApply = true;
			
			if (applyCategoryID > 0) {
				for (AuditCatData data : conAudit.getCategories()) {
					if (data.getId() == applyCategoryID) {
						data.setApplies(YesNo.Yes);
						data.setOverride(true);
					}
				}
			}
			if (removeCategoryID > 0) {
				for (AuditCatData data : conAudit.getCategories()) {
					if (data.getId() == removeCategoryID) {
						data.setApplies(YesNo.No);
						data.setOverride(true);
					}
				}
			}
		}
		// Calculate and set the percent complete
		auditPercentCalculator.percentCalculateComplete(conAudit);
		
		if (auditStatus != null && ! auditStatus.equals(conAudit.getAuditStatus())) {
			// We're changing the status
			if (!conAudit.getAuditType().isHasRequirements() && auditStatus.equals(AuditStatus.Submitted)) {
				// This audit should skip directly to Active when Submitted
				auditStatus = AuditStatus.Active;
				conAudit.setCompletedDate(new Date());
			}
			if (auditStatus.equals(AuditStatus.Submitted)) {
				conAudit.setCompletedDate(new Date());
				boolean allActive = true;
				for (ContractorAudit cAudit : getActiveAudits()) {
					if (cAudit != conAudit && cAudit.getAuditStatus().equals(AuditStatus.Pending))
						// We have to check (cAudit != conAudit) because we haven't set the status yet...it happens later 
						allActive = false;
				}
				if (allActive) {
					// Send email to contractor telling them thank you for playing
					mailer.setPermissions(permissions);
					mailer.addToken("audits", getActiveAudits());
					mailer.sendMessage(EmailTemplates.audits_thankyou, conAudit);
				}
			}
			if (auditStatus.equals(AuditStatus.Active)) {
				conAudit.setClosedDate(new Date());
				if (conAudit.getAuditType().isHasMultiple()) {
					// This audit can only have one active audit, expire the previous one
					for(ContractorAudit oldAudit : conAudit.getContractorAccount().getAudits()) {
						if (!oldAudit.equals(conAudit)) {
							if (oldAudit.getAuditType().equals(conAudit.getAuditType())
									|| (oldAudit.getAuditType().equals(AuditType.NCMS) && conAudit.getAuditType().equals(AuditType.DESKTOP))
								) {
								oldAudit.setAuditStatus(AuditStatus.Expired);
								auditDao.save(oldAudit);
							}
						}
					}
				}
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

	public AuditStatus getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(AuditStatus auditStatus) {
		this.auditStatus = auditStatus;
	}

	public void setApplyCategoryID(int applyCategoryID) {
		this.applyCategoryID = applyCategoryID;
	}

	public void setRemoveCategoryID(int removeCategoryID) {
		this.removeCategoryID = removeCategoryID;
	}

	public boolean isCanApply() {
		return isCanApply;
	}

}
