package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
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
	protected AuditQuestionDAO auditQuestionDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	protected ContractorValidator contractorValidator;
	protected UserDAO userDAO;
	protected String password1 = null;
	protected String password2 = null;

	public ContractorEdit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, UserDAO userDAO,
			InvoiceFeeDAO invoiceFeeDAO) {
		super(accountDao, auditDao);
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.userDAO = userDAO;
	}

	public void prepare() throws Exception {
		getPermissions();
		if (permissions.isLoggedIn()) {
			int conID = 0;
			if (permissions.isContractor())
				conID = permissions.getAccountId();
			else {
				permissions.tryPermission(OpPerms.AllContractors);
				conID = getParameter("id");
			}
			if (conID > 0) {
				contractor = accountDao.find(conID);

				InvoiceFee newFee = BillingCalculatorSingle.calculateAnnualFee(contractor);
				newFee = invoiceFeeDAO.find(newFee.getId());
				contractor.setNewMembershipLevel(newFee);
				user = userDAO.findByAccountID(conID, "", "No").get(0);
			}
			accountDao.clear();
		}
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
					Vector<String> errors = contractorValidator.validateContractor(contractor, password1, password2,
							user);
					if (errors.size() > 0) {
						for (String error : errors)
							addActionError(error);
						return SUCCESS;
					}
					contractor.setNeedsRecalculation(true);
					contractor.setNameIndex();

					contractor = accountDao.save(contractor);
					user.setEmail(contractor.getEmail());
					userDAO.save(user);

					addActionMessage("Successfully modified " + contractor.getName());
				}
			} else if (button.equalsIgnoreCase("Delete")) {
				permissions.tryPermission(OpPerms.RemoveContractors);
				findContractor();
				Iterator<ContractorAudit> auditList = contractor.getAudits().iterator();

				while (auditList.hasNext()) {
					ContractorAudit cAudit = auditList.next();
					if (cAudit.getAuditStatus().isPending() && cAudit.getPercentComplete() == 0) {
						auditList.remove();
						auditDao.remove(cAudit);
					}
				}

				if (contractor.getAudits().size() > 0) {
					addActionError("Cannot Remove Contractor with Audits");
					return SUCCESS;
				}

				Iterator<User> userList = contractor.getUsers().iterator();

				while (userList.hasNext()) {
					User user = userList.next();
					userList.remove();
					userDAO.remove(user);
				}

				accountDao.remove(contractor, getFtpDir());

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
