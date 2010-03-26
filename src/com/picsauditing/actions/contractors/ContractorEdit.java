package com.picsauditing.actions.contractors;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmailQueueDAO;
import com.picsauditing.dao.EmailSubscriptionDAO;
import com.picsauditing.dao.FileDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.EmailSubscription;
import com.picsauditing.jpa.entities.FileBase;
import com.picsauditing.jpa.entities.Invoice;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.Subscription;
import com.picsauditing.mail.SubscriptionTimePeriod;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.ReportFilterContractor;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorEdit extends ContractorActionSupport implements Preparable {
	private File logo = null;
	private String logoContentType = null;
	private String logoFileName = null;
	private File brochure = null;
	private String brochureFileName = null;
	protected AuditQuestionDAO auditQuestionDAO;
	private InvoiceFeeDAO invoiceFeeDAO;
	protected ContractorValidator contractorValidator;
	protected OperatorAccountDAO operatorAccountDAO;
	protected UserDAO userDAO;
	protected EmailQueueDAO emailQueueDAO;
	protected NoteDAO noteDAO;
	protected EmailSubscriptionDAO subscriptionDAO;
	protected int[] operatorIds = new int[300];
	protected Country country;
	protected State state;
	protected State billingState;
	protected int contactID;
	protected FileDAO fileDAO;

	public ContractorEdit(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, UserDAO userDAO,
			InvoiceFeeDAO invoiceFeeDAO, OperatorAccountDAO operatorAccountDAO, EmailQueueDAO emailQueueDAO,
			NoteDAO noteDAO, EmailSubscriptionDAO subscriptionDAO, FileDAO fileDAO) {
		super(accountDao, auditDao);
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.invoiceFeeDAO = invoiceFeeDAO;
		this.userDAO = userDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.emailQueueDAO = emailQueueDAO;
		this.noteDAO = noteDAO;
		this.subscriptionDAO = subscriptionDAO;
		this.fileDAO = fileDAO;
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
				int i = 0;
				for (ContractorOperator conOperator : contractor.getNonCorporateOperators()) {
					operatorIds[i] = conOperator.getOperatorAccount().getId();
					i++;
				}
			}
			accountDao.clear();

			String[] countryIsos = (String[]) ActionContext.getContext().getParameters().get("country.isoCode");
			if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
				country = getCountryDAO().find(countryIsos[0]);

			String[] stateIsos = (String[]) ActionContext.getContext().getParameters().get("state.isoCode");
			if (stateIsos != null && stateIsos.length > 0 && !Strings.isEmpty(stateIsos[0]))
				state = getStateDAO().find(stateIsos[0]);

			String[] billingStateIsos = (String[]) ActionContext.getContext().getParameters().get(
					"billingState.isoCode");
			if (billingStateIsos != null && billingStateIsos.length > 0 && !Strings.isEmpty(billingStateIsos[0]))
				billingState = getStateDAO().find(billingStateIsos[0]);
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
						
						FileBase fileBase = new FileBase(logo, logoContentType, logoFileName, PICSFileType.logos, id);
						fileDAO.save(fileBase);
						// TODO add Logo Hash
						//contractor.setLogoHash(fileBase.getFileHash());
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

					if (country != null && !country.equals(contractor.getCountry())) {
						contractor.setCountry(country);
					}

					if (state != null && !"".equals(state.getIsoCode()) && !state.equals(contractor.getState())) {
						contractor.setState(state);
					}

					if (billingState != null && !"".equals(billingState.getIsoCode())
							&& !billingState.equals(contractor.getBillingState())) {
						contractor.setBillingState(billingState);
					}

					Vector<String> errors = contractorValidator.validateContractor(contractor);
					if (errors.size() > 0) {
						for (String error : errors)
							addActionError(error);
						return SUCCESS;
					}
					contractor.setQbSync(true);
					contractor.setNeedsRecalculation(true);
					contractor.setNameIndex();

					if (contactID > 0 && contactID != contractor.getPrimaryContact().getId()) {
						contractor.setPrimaryContact(userDAO.find(contactID));
					}

					accountDao.save(contractor);

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
			} else if (button.equals("Reactivate")) {
				contractor.setRenew(true);
				if (contractor.getNewMembershipLevel().isFree())
					contractor.setStatus(AccountStatus.Active);

				accountDao.save(contractor);
				this.addNote(contractor, "Reactivated account");
				this.addActionMessage("Successfully reactivated this contractor account. "
						+ "<a href='BillingDetail.action?id=" + id + "'>Click to Create their invoice</a>");
			} else if (button.equals("Cancel")) {
				if (Strings.isEmpty(contractor.getReason())) {
					addActionError("Please select a deactivation reason before you cancel the account");
				} else {
					contractor.setRenew(false);
					if (contractor.getNewMembershipLevel().isFree())
						contractor.setStatus(AccountStatus.Deactivated);

					String expiresMessage = "";
					if (contractor.getPaymentExpires().after(new Date()))
						expiresMessage = " This account will no longer be visible to operators after "
								+ contractor.getPaymentExpires();
					else {
						expiresMessage = " This account is no longer visible to operators.";
						contractor.setStatus(AccountStatus.Deactivated);
					}
					accountDao.save(contractor);

					this.addNote(contractor, "Closed contractor account." + expiresMessage);
					this.addActionMessage("Successfully closed this contractor account." + expiresMessage);
				}
			} else if (button.equals("SendDeactivationEmail")) {
				permissions.tryPermission(OpPerms.EmailOperators);
				Set<String> emailAddresses = new HashSet<String>();
				if (operatorIds != null) {
					for (int operatorID : operatorIds) {
						List<EmailSubscription> subscriptions = subscriptionDAO.find(
								Subscription.ContractorRegistration, operatorID);
						Set<String> emails = new HashSet<String>();
						for (EmailSubscription subscription : subscriptions) {
							if (!subscription.getTimePeriod().equals(SubscriptionTimePeriod.None))
								emails.add(subscription.getUser().getEmail());
							else {
								// only want to access dao if no subscription exists (very slow operation)
								OperatorAccount operator = operatorAccountDAO.find(operatorID);
								if(operator != null && operator.getPrimaryContact() != null)
									emails.add(operator.getPrimaryContact().getEmail());
							}
						}
						if (emails.size() > 0)
							emailAddresses.addAll(emails);
						else
							addActionError("No primary contact or 'Contractor Registration' Subscriber for Operator ID: "
									+ operatorID);
					}

					if (emailAddresses.size() > 0) {
						if (!accountDao.isContained(contractor)) {
							contractor = accountDao.find(contractor.getId());
						}
						EmailBuilder emailBuilder = new EmailBuilder();
						emailBuilder.setTemplate(51); // Deactivation Email for
						// operators
						emailBuilder.setPermissions(permissions);
						emailBuilder.setContractor(contractor, OpPerms.ContractorAdmin);
						emailBuilder.setBccAddresses(Strings.implode(emailAddresses, ","));
						emailBuilder.setCcAddresses("");
						emailBuilder.setToAddresses("aharker@picsauditing.com");
						emailBuilder.setFromAddress("\"PICS Billing\"<billing@picsauditing.com>");
						EmailQueue email = emailBuilder.build();
						email.setPriority(50);
						emailQueueDAO.save(email);

						Note note = new Note();
						note.setAccount(contractor);
						note.setAuditColumns(permissions);
						note.setSummary("Deactivation Email Sent to the following email Addresses");
						note.setBody(Strings.implode(emailAddresses, ","));
						note.setPriority(LowMedHigh.Med);
						note.setNoteCategory(NoteCategory.General);
						note.setViewableById(Account.PicsID);
						note.setCanContractorView(false);
						note.setStatus(NoteStatus.Closed);
						noteDAO.save(note);

						this.addActionMessage("Successfully sent the email to operators");
					}
				}
			} else if (button.equals("copyPrimary")) {
				contractor.setBillingAddress(contractor.getAddress());
				contractor.setBillingCity(contractor.getCity());
				contractor.setBillingState(contractor.getState());
				contractor.setBillingZip(contractor.getZip());
				accountDao.save(contractor);
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
		this.logoContentType = logoContentType;
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

	public int[] getOperatorIds() {
		return operatorIds;
	}

	public void setOperatorIds(int[] operatorIds) {
		this.operatorIds = operatorIds;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public State getBillingState() {
		return billingState;
	}

	public void setBillingState(State billingState) {
		this.billingState = billingState;
	}

	public List<Invoice> getUnpaidInvoices() {
		List<Invoice> unpaidInvoices = new ArrayList<Invoice>();
		if (!contractor.isRenew()) {
			for (Invoice item : contractor.getSortedInvoices()) {
				if (item.getStatus().isUnpaid()) {
					unpaidInvoices.add(item);
				}
			}

		}
		return unpaidInvoices;
	}

	public String[] getDeactivationReasons() {
		return ReportFilterContractor.DEACTIVATION_REASON;
	}

	public List<User> getUserList() {
		return userDAO.findByAccountID(contractor.getId(), "Yes", "No");
	}

	public int getContactID() {
		return contactID;
	}

	public void setContactID(int contactID) {
		this.contactID = contactID;
	}

	public LowMedHigh getRiskLevel() {
		return contractor.getRiskLevel();
	}

	public void setRiskLevel(LowMedHigh riskLevel) {
		String userName = userDAO.find(permissions.getUserId()).getName();
		String newRiskLevel = riskLevel.toString();
		String oldRiskLevel = contractor.getRiskLevel().toString();
		if (oldRiskLevel.equals("Med"))
			oldRiskLevel = "Medium";
		if (newRiskLevel.equals("Med"))
			newRiskLevel = "Medium";

		Note note = new Note();
		note.setAccount(contractor);
		note.setAuditColumns(permissions);
		note.setSummary(userName + " changed the risk level to " + newRiskLevel + " from " + oldRiskLevel);
		note.setNoteCategory(NoteCategory.General);
		note.setCanContractorView(false);
		note.setViewableById(Account.EVERYONE);

		contractor.setRiskLevel(riskLevel);
		noteDAO.save(note);
	}
}
