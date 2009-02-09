package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.PICS.FlagCalculator2;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.FileUtils;

@SuppressWarnings("serial")
public class ContractorEdit extends ContractorActionSupport implements Preparable {
	private File logo = null;
	private String logoFileName = null;
	private File brochure = null;
	private String brochureFileName = null;
	protected User user;
	protected AuditBuilder auditBuilder;
	protected FlagCalculator2 flagCalculator2;
	protected AuditQuestionDAO auditQuestionDAO;
	protected ContractorValidator contractorValidator;
	protected UserDAO userDAO;
	protected String password1 = null;
	protected String password2 = null;

	public ContractorEdit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder,
			FlagCalculator2 flagCalculator2, AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, UserDAO userDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.flagCalculator2 = flagCalculator2;
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.userDAO = userDAO;
	}

	public void prepare() throws Exception {
		getPermissions();
		int conID = 0;
		if (permissions.isContractor())
			conID = permissions.getAccountId();
		else {
			permissions.tryPermission(OpPerms.AllContractors);
			conID = getParameter("id");
		}
		if (conID > 0) {
			contractor = accountDao.find(conID);
			user = userDAO.findByAccountID(conID,"", "No").get(0);
		}
		accountDao.clear();
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			String ftpDir = getFtpDir();

			if (button.equalsIgnoreCase("Save")) {
				if (permissions.isContractor() || permissions.hasPermission(OpPerms.ContractorAccounts, OpType.Edit)) {
					if (logo != null) {
						String extension = logoFileName.substring(logoFileName.lastIndexOf(".") + 1);
						String[] validExtensions = { "jpg", "gif", "png" };

						if (!FileUtils.checkFileExtension(extension, validExtensions)) {
							addActionError("Logos must be a jpg, gif or png image");
							return SUCCESS;
						}
						String fileName = "logo_" + contractor.getId();
						FileUtils.moveFile(logo, ftpDir, "/logos/", fileName, extension, true);
						contractor.setLogoFile(fileName + "." + extension);
					}

					if (brochure != null) {
						String extension = brochureFileName.substring(brochureFileName.lastIndexOf(".") + 1);
						String[] validExtensions = { "jpg", "gif", "png", "doc", "pdf" };

						if (!FileUtils.checkFileExtension(extension, validExtensions)) {
							addActionError("Brochure must be a image, doc or pdf file");
							return SUCCESS;
						}
						String fileName = "brochure_" + contractor.getId();
						FileUtils.moveFile(brochure, ftpDir, "/files/brochures/", fileName, extension, true);
						contractor.setBrochureFile(extension);
					}
					Vector<String> errors = contractorValidator.validateContractor(contractor, password1, password2, user);
					if (errors.size() > 0) {
						for(String error : errors)
							addActionError(error);
						return SUCCESS;
					}

					contractor = accountDao.save(contractor);
					userDAO.save(user);
					auditBuilder.buildAudits(contractor);
					BillingCalculatorSingle billCalculatorSingle = new BillingCalculatorSingle();
					InvoiceFee invoiceFee = billCalculatorSingle.calculateAnnualFee(contractor);
					contractor.setNewMembershipLevel(invoiceFee);
					contractor = accountDao.save(contractor);
					flagCalculator2.runByContractor(contractor.getId());
					addActionMessage("Successfully modified " + contractor.getName());
				}
			} else if (button.equalsIgnoreCase("Delete")) {
				permissions.tryPermission(OpPerms.RemoveContractors);
				findContractor();
				if (contractor.getAudits().size() > 0) {
					addActionError("Cannot Remove Contractor with Audits");
					return SUCCESS;
				}
				accountDao.remove(contractor, getFtpDir());
				userDAO.remove(user);
				return "ConList";
			} else {
				// Because there are anomalies between browsers and how they
				// pass
				// in the button values, this is a catch all so we can get
				// notified
				// when the button name isn't set correctly
				throw new Exception("no button action found called " + button);
			}
		}

		this.subHeading = "Contractor Edit";

		return SUCCESS;
	}

	public void setLogo(File logo) {
		this.logo = logo;
	}

	public void setBrochure(File brochure) {
		this.brochure = brochure;
	}

	public void setLogoContentType(String logoContentType) {
	}

	public void setLogoFileName(String logoFileName) {
		this.logoFileName = logoFileName;
	}

	public void setBrochureContentType(String brochureContentType) {
	}

	public void setBrochureFileName(String brochureFileName) {
		this.brochureFileName = brochureFileName;
	}

	public List<AuditQuestion> getTradeList() throws Exception {
		return auditQuestionDAO.findQuestionByType("Service");
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
