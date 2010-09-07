package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.NcmsCategory;
import com.picsauditing.jpa.entities.OshaType;

/**
 * Used by Audit.action to show a list of categories for a given audit. Also
 * allows users to change the status of an audit.
 */
@SuppressWarnings("serial")
public class ContractorAuditAction extends AuditCategorySingleAction {

	protected ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	protected OshaAuditDAO oshaAuditDAO;

	private boolean isCanApply = false;
	private int applyCategoryID = 0;
	private int removeCategoryID = 0;

	public ContractorAuditAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorAuditOperatorDAO caoDAO, AuditCategoryDAO categoryDAO, AuditCategoryDataDAO catDataDao,
			AuditDataDAO auditDataDao, AuditPercentCalculator auditPercentCalculator,
			ContractorAuditOperatorDAO contractorAuditOperatorDAO, CertificateDAO certificateDao,
			OshaAuditDAO oshaAuditDAO, OperatorAccountDAO opDAO) {
		super(accountDao, auditDao, caoDAO, categoryDAO, catDataDao, auditDataDao, auditPercentCalculator,
				certificateDao, opDAO);
		this.contractorAuditOperatorDAO = contractorAuditOperatorDAO;
		this.oshaAuditDAO = oshaAuditDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		this.findConAudit();

		if (isSingleCat())
			ServletActionContext.getResponse().sendRedirect(
					"AuditCat.action?auditID=" + auditID + "&catDataID=" + getCategories().get(0).getId());

		if (permissions.isPicsEmployee()) {
			isCanApply = true;

			if (applyCategoryID > 0) {
				for (AuditCatData data : conAudit.getCategories()) {
					if (data.getId() == applyCategoryID) {
						data.setApplies(true);
						data.setOverride(true);
					}
				}
			}
			if (removeCategoryID > 0) {
				for (AuditCatData data : conAudit.getCategories()) {
					if (data.getId() == removeCategoryID) {
						data.setApplies(true);
						data.setOverride(true);
					}
				}
			}
		}

		//TODO: fix this auditStatus reference
		if (conAudit.getAuditType().isAnnualAddendum()/* && conAudit.getAuditStatus().isSubmitted()*/) {
			for (AuditCatData auditCatData : conAudit.getCategories()) {
				if (!auditCatData.isApplies()) {
					if (auditCatData.getCategory().isSha()) {
						switch (auditCatData.getCategory().getId()) {
						case AuditCategory.OSHA_AUDIT:
							oshaAuditDAO.removeByType(conAudit.getId(), OshaType.OSHA);
							break;
						case AuditCategory.MSHA:
							oshaAuditDAO.removeByType(conAudit.getId(), OshaType.MSHA);
							break;
						case AuditCategory.CANADIAN_STATISTICS:
							oshaAuditDAO.removeByType(conAudit.getId(), OshaType.COHS);
							break;
						}
					} else {
						auditDataDao.removeDataByCategory(conAudit.getId(), auditCatData.getCategory().getId());
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
		if (conAudit.getAuditType().isDesktop()) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 1461);
		}
		if (conAudit.getAuditType().getId() == 3) {
			auditData = auditDataDao.findAnswerToQuestion(conAudit.getId(), 2432);
		}
		if (auditData != null)
			return auditData.getAnswer();

		return null;
	}

}
