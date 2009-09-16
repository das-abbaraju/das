package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.jpa.entities.YesNo;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class ContractorAuditAction extends AuditCategorySingleAction {

	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;

	private boolean isCanApply = false;
	private int applyCategoryID = 0;
	private int removeCategoryID = 0;

	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, ContractorAuditOperatorDAO caoDAO,
			AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator,
			AuditBuilder auditBuilder, ContractorAuditOperatorDAO contractorAuditOperatorDAO, CertificateDAO certificateDao) {
		super(accountDao, auditDao, caoDAO, catDataDao, auditDataDao, auditPercentCalculator, auditBuilder, certificateDao);
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findConAudit();

		// Some stuff like rebuilding categories and percentages doesn't have to
		// be done everytime
		auditBuilder.fillAuditCategories(conAudit, "recalculate".equals(button));

		if (isSingleCat())
			ServletActionContext.getResponse().sendRedirect(
					"AuditCat.action?auditID=" + auditID + "&catDataID=" + getCategories().get(0).getId());

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

		super.execute();

		if (this.conAudit.getAuditType().getId() == AuditType.NCMS)
			return "NCMS";

		return SUCCESS;
	}

	public List<NcmsCategory> getNcmsCategories() {
		try {
			return catDataDao.findNcmsCategories(this.id);
		} catch (Exception e) {
			List<NcmsCategory> error = new ArrayList<NcmsCategory>();
			error.add(new NcmsCategory("Error retrieving list", ""));
			return error;
		}
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
	
	public String getAuditorNotes() {
		AuditData auditData = null;
		if(conAudit.getAuditType().isDesktop()) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 1461);
		}
		if(conAudit.getAuditType().getId() == 3) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 2432);
		}
		if(auditData != null)
			return auditData.getAnswer();

		return null;	
	}
}
