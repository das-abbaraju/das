package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.PICS.DateBean;
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
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.jpa.entities.NoteCategory;
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
	protected AuditPercentCalculator auditPercentCalculator;
	protected AuditBuilder auditBuilder;
	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;

	private boolean isCanApply = false;
	private int applyCategoryID = 0;
	private int removeCategoryID = 0;

	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator,
			AuditBuilder auditBuilder, ContractorAuditOperatorDAO contractorAuditOperatorDAO) {
		super(accountDao, auditDao, catDataDao, auditDataDao);
		this.auditPercentCalculator = auditPercentCalculator;
		this.auditBuilder = auditBuilder;
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		this.findConAudit();

		// Some stuff like rebuilding categories and percentages doesn't have to
		// be done everytime

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
		// Calculate and set the percent complete
		auditPercentCalculator.percentCalculateComplete(conAudit, conAudit.getAuditType().getId() == 17);

		if ("Submit".equals(button)) {
			if (conAudit.getAuditType().isPqf()) {
				if (conAudit.getAuditStatus().equals(AuditStatus.Active) && conAudit.getPercentVerified() == 100) {
					auditStatus = AuditStatus.Active;
					if(conAudit.isAboutToExpire())
						conAudit.setCompletedDate(new Date());
				}	
				else if (conAudit.getAuditStatus().isActiveResubmittedExempt())
					auditStatus = AuditStatus.Resubmitted;
				else
					auditStatus = AuditStatus.Submitted;
				conAudit.setExpiresDate(DateBean.getMarchOfNextYear(new Date()));
			} else if (conAudit.getAuditType().isHasRequirements() || conAudit.getAuditType().isMustVerify())
				auditStatus = AuditStatus.Submitted;
			else
				auditStatus = AuditStatus.Active;
		}

		if (auditStatus != null && !auditStatus.equals(conAudit.getAuditStatus())) {
			// We're changing the status
			if (auditStatus.equals(AuditStatus.Active)) {
				conAudit.setClosedDate(new Date());
				if (!conAudit.getAuditType().isHasMultiple()) {
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

				if (conAudit.getAuditType().isAnnualAddendum()
						&& DateBean.getCurrentYear() - 1 == Integer.parseInt(conAudit.getAuditFor())) {
					// We're activating the most recent year's audit (ie 2008)
					for (ContractorAudit audit : contractor.getAudits()) {
						if (audit.getAuditType().isAnnualAddendum()
								&& Integer.parseInt(audit.getAuditFor()) < DateBean.getCurrentYear() - 3
								&& !audit.getAuditStatus().isExpired()) {
							// Any annual audit before 2006 (ie 2005)
							audit.setAuditStatus(AuditStatus.Expired);
							auditDao.save(audit);
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
				String notes = "";
				if (conAudit.getAuditType().isPqf()) {
					// Add a note...
					// TODO we should probably stop doing this...it's kind of
					// pointless or at least we should do it for other audits
					// too
					notes = conAudit.getContractorAccount().getName() + " Submitted their PQF ";
				}
				int typeID = conAudit.getAuditType().getId();
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

					notes = conAudit.getAuditType().getAuditName()
							+ " Submission email sent for outstanding requirements.";
				} else
					notes = conAudit.getAuditType().getAuditName() + " Submitted";

				addNote(conAudit.getContractorAccount(), notes, NoteCategory.Audits);
			}

			// Save the audit status
			conAudit.setAuditStatus(auditStatus);
			auditDao.save(conAudit);

			ContractorAccount contractorAccount = conAudit.getContractorAccount();
			contractor.setNeedsRecalculation(true);
			accountDao.save(contractorAccount);
		}

		if (this.conAudit.getAuditType().getId() == AuditType.NCMS)
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
			if (permissions.isContractor() && !conAudit.getContractorAccount().isPaymentMethodStatusValid()) {
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
		if (conAudit.getAuditType().isMustVerify())
			return false;
		if (conAudit.getAuditStatus().equals(AuditStatus.Submitted)
				|| conAudit.getAuditStatus().equals(AuditStatus.Resubmitted))
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
