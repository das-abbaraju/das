package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
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

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 * 
 * @author Trevor
 * 
 */
public class ContractorAuditAction extends AuditActionSupport {
	protected AuditStatus auditStatus;
	protected FlagCalculator2 flagCalculator;
	protected AuditPercentCalculator auditPercentCalculator;
	protected AuditBuilder auditBuilder;

	private boolean isCanApply = false;
	private int applyCategoryID = 0;
	private int removeCategoryID = 0;

	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, 
			FlagCalculator2 flagCalculator2, AuditPercentCalculator auditPercentCalculator, AuditBuilder auditBuilder) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.flagCalculator = flagCalculator2;
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();
		
		// TODO pass this in or set it to true before releasing
		boolean fullLoad = false;

		if (fullLoad)
			auditBuilder.fillAuditCategories(conAudit);

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
		if (fullLoad)
			// Calculate and set the percent complete
			auditPercentCalculator.percentCalculateComplete(conAudit);

		if (auditStatus != null && !auditStatus.equals(conAudit.getAuditStatus())) {
			// We're changing the status
			if (!conAudit.getAuditType().isHasRequirements() && auditStatus.equals(AuditStatus.Submitted)) {
				// This audit should skip directly to Active when Submitted
				auditStatus = AuditStatus.Active;
			}
			if (auditStatus.equals(AuditStatus.Active)) {
				conAudit.setClosedDate(new Date());
				if (conAudit.getAuditType().isHasMultiple()) {
					// This audit can only have one active audit, expire the
					// previous one
					for (ContractorAudit oldAudit : conAudit.getContractorAccount().getAudits()) {
						if (!oldAudit.equals(conAudit)) {
							if (oldAudit.getAuditType().equals(conAudit.getAuditType())
									|| (oldAudit.getAuditType().equals(AuditType.NCMS) && conAudit.getAuditType()
											.equals(AuditType.DESKTOP))) {
								oldAudit.setAuditStatus(AuditStatus.Expired);
								auditDao.save(oldAudit);
							}
						}
					}
				}

				emailContractorOnAudit();
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
			if (conAudit.getAuditType().getAuditTypeID() == AuditType.PQF && auditStatus.equals(AuditStatus.Submitted)) {
				ContractorBean cBean = new ContractorBean();
				cBean.setFromDB(conAudit.getContractorAccount().getIdString());
				String notes = conAudit.getContractorAccount().getName() + " Submitted their PQF ";
				cBean.addNote(conAudit.getContractorAccount().getIdString(), permissions.getName(), notes, DateBean
						.getTodaysDate());
				cBean.writeToDB();
			}
			auditDao.save(conAudit);

			flagCalculator.runByContractor(conAudit.getContractorAccount().getId());
		}

		if (this.conAudit.getAuditType().getAuditTypeID() == AuditType.NCMS)
			return "NCMS";

		return SUCCESS;
	}

	/**
	 * Can the current user submit this audit in its current state?
	 * 
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
	 * 
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
