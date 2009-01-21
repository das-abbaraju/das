package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.ContractorBean;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NcmsCategoryDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

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
	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;

	private boolean isCanApply = false;
	private int applyCategoryID = 0;
	private int removeCategoryID = 0;

	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, FlagCalculator2 flagCalculator2,
			AuditPercentCalculator auditPercentCalculator, AuditBuilder auditBuilder,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.flagCalculator = flagCalculator2;
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		if ("Submit".equals(button)) {
			if (conAudit.getAuditType().isPqf()) {
				if (conAudit.getAuditStatus().equals(AuditStatus.Active) && conAudit.getPercentVerified() == 100)
					conAudit.setAuditStatus(AuditStatus.Active);
				else if (conAudit.getAuditStatus().isActiveResubmittedExempt())
					conAudit.setAuditStatus(AuditStatus.Resubmitted);
				else
					conAudit.setAuditStatus(AuditStatus.Submitted);
				conAudit.setExpiresDate(DateBean.getMarchOfNextYear(new Date()));
			} else if (conAudit.getAuditType().isHasRequirements() || conAudit.getAuditType().isMustVerify())
				conAudit.setAuditStatus(AuditStatus.Submitted);
			else
				conAudit.setAuditStatus(AuditStatus.Active);
			auditDao.save(conAudit);
			return SUCCESS;
		}

		if ("Approve".equals(button)) {
			conAudit.setAuditStatus(AuditStatus.Active);
			auditDao.save(conAudit);
			return SUCCESS;
		}

		if ("Reject".equals(button)) {
			conAudit.setAuditStatus(AuditStatus.Pending);
			auditDao.save(conAudit);
			return SUCCESS;
		}

		// Some stuff like rebuilding categories and percentages doesn't have to
		// be done everytime
		boolean fullLoad = true;
		// Try and guess to see if we need it or not
		if (auditStatus != null)
			fullLoad = false;
		if (conAudit.getAuditType().isAnnualAddendum() && conAudit.getAuditStatus().isPendingSubmitted())
			fullLoad = true;
		if (conAudit.getAuditStatus().equals(AuditStatus.Active))
			fullLoad = false;

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
				Date dateToExpire = DateBean.addMonths(conAudit.getCompletedDate(), conAudit.getAuditType()
						.getMonthsToExpire());
				conAudit.setExpiresDate(dateToExpire);
			}

			if (auditStatus.equals(AuditStatus.Submitted)) {
				if (conAudit.getAuditType().isPqf()) {
					// Add a note...
					// TODO we should probably stop doing this...it's kind of
					// pointless or at least we should do it for other audits
					// too
					ContractorBean cBean = new ContractorBean();
					cBean.setFromDB(conAudit.getContractorAccount().getIdString());
					String notes = conAudit.getContractorAccount().getName() + " Submitted their PQF ";
					cBean.addNote(conAudit.getContractorAccount().getIdString(), permissions.getName(), notes, DateBean
							.getTodaysDate());
					cBean.writeToDB();
				}
				int typeID = conAudit.getAuditType().getAuditTypeID();
				if (typeID == AuditType.DESKTOP || typeID == AuditType.DA) {
					EmailBuilder emailBuilder = new EmailBuilder();

					// TODO combine these 2 templates
					if (typeID == AuditType.DESKTOP)
						emailBuilder.setTemplate(7); // Desktop Submission
					else
						emailBuilder.setTemplate(8); // D&A Submission

					emailBuilder.setPermissions(permissions);
					emailBuilder.setConAudit(conAudit);
					EmailSender.send(emailBuilder.build());

				}

			}

			// Save the audit status
			conAudit.setAuditStatus(auditStatus);
			auditDao.save(conAudit);

			boolean activeAnnualUpdate = false;
			if (conAudit.getAuditType().isAnnualAddendum() && conAudit.getAuditStatus().equals(AuditStatus.Active)
					&& DateBean.getCurrentYear() - 1 == Integer.parseInt(conAudit.getAuditFor())) {
				activeAnnualUpdate = true;
				for (ContractorAudit audit : contractor.getAudits()) {
					if (activeAnnualUpdate && audit.getAuditType().isAnnualAddendum()
							&& Integer.parseInt(audit.getAuditFor()) == DateBean.getCurrentYear() - 4)
						audit.setAuditStatus(AuditStatus.Expired);
					auditDao.save(audit);
				}
			}

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
		if (conAudit.getAuditStatus().equals(AuditStatus.Pending)) {
			if (permissions.isContractor() 
					&& !conAudit.getContractorAccount().isPaymentMethodStatusValid()) {
					return false;
			}
			return true;
		}
		if (conAudit.getAuditType().isPqf()) {
			// PQFs are perpetual audits and can be renewed
			if (permissions.isContractor()) {
				// We don't allow admins to resubmit audits (only contractors)
				if (conAudit.getAuditStatus().equals(AuditStatus.Expired))
					return true;
				if (conAudit.isAboutToExpire())
					return true;
			}
		}
		return false;
	}

	/**
	 * Can the current user submit this audit in its current state?
	 * 
	 * @return
	 */
	public boolean isCanClose() {
		if (permissions.isContractor())
			return false;
		if (permissions.hasPermission(OpPerms.InsuranceVerification)
				&& conAudit.getAuditType().getClassType().equals(AuditTypeClass.Policy)) {
			if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)
					|| conAudit.getAuditStatus().equals(AuditStatus.Resubmitted))
				return true;
		}
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

	public List<MenuComponent> getAuditMenu() {
		List<MenuComponent> menu = super.getAuditMenu();

		if (conAudit != null) {
			for (MenuComponent comp : menu) {
				if (comp.getAuditId() == conAudit.getId()) {
					comp.setCurrent(true);
					break;
				}

				if (comp.getChildren() != null) {
					for (MenuComponent child : comp.getChildren()) {
						if (child.getAuditId() == conAudit.getId()) {
							child.setCurrent(true);
							return menu;
						}
					}
				}
			}
		}

		return menu;
	}

}
