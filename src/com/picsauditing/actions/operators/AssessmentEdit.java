package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.AccountActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class AssessmentEdit extends AccountActionSupport implements Preparable {
	protected AccountDAO accountDAO;
	protected UserDAO userDAO;

	protected Account center;
	protected Country country;
	protected State state;
	protected int contactID;

	public AssessmentEdit(AccountDAO accountDAO, UserDAO userDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
	}

	public void prepare() throws Exception {
		loadPermissions();
		
		id = getParameter("id");
		
		if (permissions.isAssessment())
			id = permissions.getAccountId();

		if (id > 0) {
			center = accountDAO.find(id);
			subHeading = "Edit Assessment Center";
		} else {
			center = new Account();
		}

		String[] countryIsos = (String[]) ActionContext.getContext().getParameters().get("country.isoCode");
		if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
			country = getCountryDAO().find(countryIsos[0]);

		String[] stateIsos = (String[]) ActionContext.getContext().getParameters().get("state.isoCode");
		if (stateIsos != null && stateIsos.length > 0 && !Strings.isEmpty(stateIsos[0]))
			state = getStateDAO().find(stateIsos[0]);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (permissions.isAdmin())
			tryPermissions(OpPerms.ManageAssessment);
		else if (!permissions.isAssessment())
			throw new NoRightsException("Admin or Assessment Center");

		if (button != null) {
			if (button.equalsIgnoreCase("Save")) {
				if (country != null && !country.equals(center.getCountry()))
					center.setCountry(country);
				if (state != null && !"".equals(state.getIsoCode()) && !state.equals(center.getState()))
					center.setState(state);

				List<String> errors = validateAccount(center);
				if (errors.size() > 0) {
					for (String error : errors)
						addActionError(error);

					return SUCCESS;
				}

				if (permissions.hasPermission(OpPerms.ManageAssessment, OpType.Edit)) {
					center.setType("Assessment");
					center.setAuditColumns(permissions);
					center.setNameIndex();
				}

				if (contactID > 0 && (center.getPrimaryContact() == null 
						|| contactID != center.getPrimaryContact().getId()))
					center.setPrimaryContact(userDAO.find(contactID));

				if (center.getId() == 0) {
					Naics naics = new Naics();
					naics.setCode("0");
					center.setNaics(naics);
					center.setNaicsValid(false);

					if (center.getName().length() > 19) {
						// Get acronym if there are any spaces?
						if (center.getName().contains(" "))
							center.setQbListID("NOLOAD"	+ center.getName().replaceAll("(\\w)(\\w+\\s?)", "$1"));
						else
							center.setQbListID("NOLOAD" + center.getName().substring(0, 20));
					}

					center.setQbSync(false);
					center.setAcceptsBids(false);
					center.setRequiresOQ(false);
					center.setRequiresCompetencyReview(false);
				}

				center = accountDAO.save(center);
				id = center.getId();

				addActionMessage("Successfully saved " + center.getName());
			}
		}

		if (id == 0)
			subHeading = "Add Assessment Center";
		else {
			if (center.getPrimaryContact() == null)
				addActionError("Please add a primary contact to this account");
		}

		return SUCCESS;
	}

	public List<Account> getAssessmentList() throws Exception {
		return accountDAO.findWhere("a.type = 'Assessment' AND a.status IN ('Active','Pending','Demo')");
	}

	private List<String> validateAccount(Account account) {
		List<String> errorMessages = new ArrayList<String>();
		if (Strings.isEmpty(account.getName()))
			errorMessages.add("Please fill in the Company Name field");
		else if (account.getName().length() < 3)
			errorMessages.add("Your company name must be at least 3 characters long");

		if (account.getCountry() == null) {
			errorMessages.add("Please select a country");
		}
		return errorMessages;
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

	public int getContactID() {
		return contactID;
	}

	public void setContactID(int contactID) {
		this.contactID = contactID;
	}

	public Account getCenter() {
		return center;
	}

	public void setCenter(Account center) {
		this.center = center;
	}
	
	public List<User> getUsers() {
		return userDAO.findByAccountID(center.getId(), "Yes", "No");
	}
}
