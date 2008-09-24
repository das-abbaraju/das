package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.SpringUtils;

public class ContractorEdit extends ContractorActionSupport implements Preparable {
	private AuditQuestionDAO aQuestionDAO;
	private File logo = null;
	private String logoContentType = null;
	private String logoFileName = null;
	private File brochure = null;
	private String brochureContentType = null;
	private String brochureFileName = null;

	public ContractorEdit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO) {
		super(accountDao, auditDao);
		this.aQuestionDAO = auditQuestionDAO;
	}

	public void prepare() throws Exception {
		int conID = getParameter("id");
		contractor = accountDao.find(conID);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			String ftpDir = getFtpDir();
			if (button.equals("save")) {
				if (logo != null) {
					// Copy Logo to ftp-dir
					String extension = logoFileName.substring(logoFileName.lastIndexOf(".") + 1);
					String[] validExtensions = { "jpg", "gif", "png" };

					if (!FileUtils.checkFileExtension(extension, validExtensions)) {
						addActionError("Logos must be a jpg, gif or png image");
						return SUCCESS;
					}
					String fileName = "logo_" + contractor.getId();
					FileUtils.copyFile(logo, ftpDir, "/files/logos/", fileName, extension, true);
					contractor.setLogoFile(fileName + "." + extension);
				}
				if (brochure != null) {
					// Copy Logo to ftp-dir

				}

				accountDao.save(contractor);
				addActionMessage("Successfully modified " + contractor.getName());
			}
			if (button.equals("delete")) {
				permissions.tryPermission(OpPerms.RemoveContractors);
				accountDao.remove(contractor, getFtpDir());
				return SUCCESS;
			}
		}

		this.subHeading = "Contractor Edit";

		return SUCCESS;
	}

	public Industry[] getIndustryList() {
		return Industry.values();
	}

	public Map<String, String> getStateList() {
		return State.getStates(true);
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "active='Y'", permissions);
	}

	public List<AuditQuestion> getTradeList() throws Exception {
		return aQuestionDAO.findQuestionByType("Service");
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	public void setBrochure(File brochure) {
		this.brochure = brochure;
	}

	public void setLogoContentType(String logoContentType) {
		this.logoContentType = logoContentType;
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}

	public void setBrochureContentType(String brochureContentType) {
		this.brochureContentType = brochureContentType;
	}

	public void setBrochureFileName(String brochureFileName) {
		this.brochureFileName = brochureFileName;
	}
}
