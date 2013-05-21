package com.picsauditing.actions.report.oq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AssessmentEdit extends AccountActionSupport {
	@Autowired
	private UserDAO userDAO;

	private Account center;
	private User contact;

	public String execute() throws Exception {
		loadCenterIfNull();
		checkPermissions();

		subHeading = getText("AssessmentCenterEdit.title");

		if (id == 0) {
			subHeading = getText("AssessmentCenterEdit.AddAssessmentCenter");
		}

		if (center.getId() > 0 && center.getPrimaryContact() == null) {
			addActionError(getText("AssessmentCenterEdit.AddPrimaryContact"));
		}

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.ManageAssessment, type = OpType.Edit)
	public String save() throws Exception {
		List<String> errors = validateAccount(center);
		if (errors.size() > 0) {
			for (String error : errors) {
				addActionError(error);
			}

			return REDIRECT;
		}

		center.setType("Assessment");
		center.setAuditColumns(permissions);
		center.setNameIndex();

		if (contact != null) {
			center.setPrimaryContact(contact);
		}

		if (center.getId() == 0) {
			setUpNewCenter();
		}

		accountDAO.save(center);
		addActionMessage(getTextParameterized("FacilitiesEdit.SuccessfullySaved", center.getName()));

		return REDIRECT;
	}

	public List<Account> getAssessmentList() throws Exception {
		return accountDAO.findWhere("a.type = 'Assessment' AND a.status IN ('Active','Pending','Demo')");
	}

	public Account getCenter() {
		return center;
	}

	public void setCenter(Account center) {
		this.center = center;
	}

	public User getContact() {
		return contact;
	}

	public void setContact(User contact) {
		this.contact = contact;
	}

	public List<User> getUsers() {
		return userDAO.findByAccountID(center.getId(), "Yes", "No");
	}

	private void loadCenterIfNull() throws RecordNotFoundException {
		if (center == null) {
			id = getParameter("id");

			if (permissions.isAssessment()) {
				id = permissions.getAccountId();
			}

			if (id > 0) {
				center = accountDAO.find(id);
			} else {
				center = new Account();
			}
		}
	}

	private void checkPermissions() throws NoRightsException {
		if (permissions.isAdmin()) {
			tryPermissions(OpPerms.ManageAssessment);
		} else if (!permissions.isAssessment()) {
			throw new NoRightsException("Admin or Assessment Center");
		}
	}

	private List<String> validateAccount(Account account) {
		List<String> errorMessages = new ArrayList<String>();

		if (Strings.isEmpty(account.getName())) {
			errorMessages.add(getText("FacilitiesEdit.PleaseFillInCompanyName"));
		} else if (account.getName().length() < 3) {
			errorMessages.add(getText("FacilitiesEdit.NameAtLeast2Chars"));
		}

		if (account.getCountry() == null) {
			errorMessages.add(getText("FacilitiesEdit.SelectCountry"));
		}

		return errorMessages;
	}

	private void setUpNewCenter() {
		Naics naics = new Naics();
		naics.setCode("0");
		center.setNaics(naics);
		center.setNaicsValid(false);

		if (center.getName().length() > 19) {
			// Get acronym if there are any spaces?
			if (center.getName().contains(" ")) {
				center.setQbListID("NOLOAD" + center.getName().replaceAll("(\\w)(\\w+\\s?)", "$1"));
				center.setQbListCAID("NOLOAD" + center.getName().replaceAll("(\\w)(\\w+\\s?)", "$1"));
			} else {
				center.setQbListID("NOLOAD" + center.getName().substring(0, 20));
				center.setQbListCAID("NOLOAD" + center.getName().substring(0, 20));
			}
		}

		center.setQbSync(false);
		center.setSapLastSync(new Date());
		center.setRequiresOQ(false);
		center.setRequiresCompetencyReview(false);
	}
}
