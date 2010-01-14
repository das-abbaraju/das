package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WaitingOn;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport implements Preparable {
	public ContractorRegistrationRequest newContractor = new ContractorRegistrationRequest();
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	protected OperatorAccountDAO operatorAccountDAO;
	protected UserDAO userDAO;
	protected CountryDAO countryDAO;
	protected StateDAO stateDAO;
	protected ContractorAccountDAO contractorAccountDAO;
	private int requestID;
	protected Country country;
	protected State state;
	protected int requestedOperator;
	protected int requestedUser;
	protected int conID;
	protected ContractorAccount conAccount = null;

	public RequestNewContractor(ContractorRegistrationRequestDAO contractorRegistrationRequestDAO,
			OperatorAccountDAO operatorAccountDAO, UserDAO userDAO, CountryDAO countryDAO, StateDAO stateDAO,
			ContractorAccountDAO contractorAccountDAO) {
		this.contractorRegistrationRequestDAO = contractorRegistrationRequestDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.userDAO = userDAO;
		this.countryDAO = countryDAO;
		this.stateDAO = stateDAO;
		this.contractorAccountDAO = contractorAccountDAO;
	}

	public void prepare() throws Exception {
		requestID = getParameter("requestID");
		if (requestID > 0) {
			newContractor = contractorRegistrationRequestDAO.find(requestID);
		}

		String[] countryIsos = (String[]) ActionContext.getContext().getParameters().get("country.isoCode");
		if (countryIsos != null && countryIsos.length > 0 && !Strings.isEmpty(countryIsos[0]))
			country = countryDAO.find(countryIsos[0]);

		String[] stateIsos = (String[]) ActionContext.getContext().getParameters().get("state.isoCode");
		if (stateIsos != null && stateIsos.length > 0 && !Strings.isEmpty(stateIsos[0]))
			state = stateDAO.find(stateIsos[0]);
		
		if(newContractor.getContractor() != null) {
			conAccount = contractorAccountDAO.find(newContractor.getContractor().getId());
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equals("Save")) {
				if (Strings.isEmpty(newContractor.getName()))
					addActionError("Please fill the contractor Name");
				if (Strings.isEmpty(newContractor.getContact()))
					addActionError("Please fill the Contact Name");
				if (requestedOperator == 0)
					addActionError("Please select the Requested By Account");
				if (requestedUser == 0 && Strings.isEmpty(newContractor.getRequestedByUserOther()))
					addActionError("Please select the Requested User for the Account");
				if (country == null)
					addActionError("Please select a Country");
				else if (country.getIsoCode().equals("US") || country.getIsoCode().equals("CA")) {
					if (state == null || Strings.isEmpty(state.getIsoCode())) {
						addActionError("Please select a State");
					}
				}
				if (getActionErrors().size() > 0) {
					return SUCCESS;
				}

				if (newContractor.getDeadline() == null) {
					newContractor.setDeadline(DateBean.addMonths(new Date(), 3));
				}

				if (country != null && !country.equals(newContractor.getCountry())) {
					newContractor.setCountry(country);
				}
				if (state != null && !state.equals(newContractor.getState())) {
					newContractor.setState(state);
				}
				if (requestedOperator > 0
						&& (newContractor.getRequestedBy() == null || requestedOperator != newContractor
								.getRequestedBy().getId())) {
					newContractor.setRequestedBy(new OperatorAccount());
					newContractor.getRequestedBy().setId(requestedOperator);
				}
				if (requestedUser > 0
						&& (newContractor.getRequestedByUser() == null || requestedUser != newContractor
								.getRequestedByUser().getId())) {
					newContractor.setRequestedByUser(new User(requestedUser));
				}
				if (conID > 0
						&& (newContractor.getContractor() == null || conID != newContractor.getContractor().getId())) {
					newContractor.setContractor(new ContractorAccount(conID));
				}
			}
			if (button.equals("Close Request")) {
				newContractor.setOpen(false);
			}
			if (button.equals("Send Email") || button.equals("Contacted By Phone")) {
				if (button.equals("Send Email")) {
					// Send the Email
				}
				newContractor.setContactCount(newContractor.getContactCount() + 1);
				newContractor.setLastContactedBy(new User(permissions.getUserId()));
				newContractor.setLastContactDate(new Date());
			}
			newContractor.setAuditColumns(permissions);
			contractorRegistrationRequestDAO.save(newContractor);
			requestID = newContractor.getId();
			addActionMessage("Successfully saved the Contractor");
		}
		return SUCCESS;
	}

	public ContractorRegistrationRequest getNewContractor() {
		return newContractor;
	}

	public void setNewContractor(ContractorRegistrationRequest newContractor) {
		this.newContractor = newContractor;
	}

	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public List<OperatorAccount> getOperatorsWithCorporate() {
		if (permissions == null)
			return null;
		return operatorAccountDAO.findWhere(true, "", permissions);
	}

	public List<User> getUsersList() {
		return userDAO.findByAccountID(1813, "Yes", "No");
	}

	public List<Country> getCountryList() {
		return countryDAO.findAll();
	}

	public List<State> getStateList() {
		return stateDAO.findAll();
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

	public int getRequestedOperator() {
		return requestedOperator;
	}

	public void setRequestedOperator(int requestedOperator) {
		this.requestedOperator = requestedOperator;
	}

	public int getRequestedUser() {
		return requestedUser;
	}

	public void setRequestedUser(int requestedUser) {
		this.requestedUser = requestedUser;
	}

	public int getConID() {
		return conID;
	}

	public void setConID(int conID) {
		this.conID = conID;
	}

	public ContractorAccount getConAccount() {
		return conAccount;
	}
	
	public User getAssignedCSR() {
		if(newContractor.getId() > 0 
				&& newContractor.getHandledBy().equals(WaitingOn.PICS)) {
			if(newContractor.getCountry().getCsr() != null)
				return newContractor.getCountry().getCsr();
			else
				return  newContractor.getState().getCsr();
		}
		return null;
	}
}
