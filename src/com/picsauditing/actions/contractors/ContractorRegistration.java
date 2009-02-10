package com.picsauditing.actions.contractors;

import java.util.List;
import java.util.Vector;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.ContractorValidator;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class ContractorRegistration extends ContractorActionSupport {
	protected ContractorAccount contractor;
	protected User user;
	protected String confirmPassword;

	protected UserDAO userDAO;
	protected AuditQuestionDAO auditQuestionDAO;
	protected NoteDAO noteDAO;
	protected ContractorValidator contractorValidator;

	public ContractorRegistration(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			AuditQuestionDAO auditQuestionDAO, ContractorValidator contractorValidator, NoteDAO noteDAO, UserDAO userDAO) {
		super(accountDao, auditDao);
		this.auditQuestionDAO = auditQuestionDAO;
		this.contractorValidator = contractorValidator;
		this.noteDAO = noteDAO;
		this.userDAO = userDAO;
		this.subHeading = "New Contractor Information";
	}

	public String execute() throws Exception {

		if ("Register".equalsIgnoreCase(button)) {
			contractor.setType("Contractor");
			contractor.setActive('N');
			Vector<String> errors = contractorValidator.validateContractor(contractor, user.getPassword(),
					confirmPassword, user);
			if (errors.size() > 0) {
				for (String error : errors)
					addActionError(error);
				return SUCCESS;
			}
			contractor.setAuditColumns(new User(User.CONTRACTOR));
			contractor = accountDao.save(contractor);
			user.setIsActive(YesNo.No);
			user.setAccount(contractor);
			user.setAuditColumns(new User(User.CONTRACTOR));
			user.setIsGroup(YesNo.No);
			user.setName(contractor.getContact());
			user = userDAO.save(user);

			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(2); // Welcome Email
			emailBuilder.setContractor(contractor);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(90);
			EmailSender.send(emailQueue);

			Note note = new Note();
			note.setAccount(contractor);
			note.setAuditColumns(new User(User.SYSTEM));
			note.setSummary("Welcome Email Sent");
			note.setPriority(LowMedHigh.Low);
			note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);
			
			Permissions permissions = new Permissions();
			permissions.login(user);
			ActionContext.getContext().getSession().put("permissions", permissions);
			
			ServletActionContext.getResponse().sendRedirect("ContractorFacilities.action?id=" + contractor.getId());
			this.addActionMessage("Redirected to Facilities Page");
			return BLANK;
		}

		return SUCCESS;
	}

	public List<AuditQuestion> getTradeList() throws Exception {
		return auditQuestionDAO.findQuestionByType("Service");
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
