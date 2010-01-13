package com.picsauditing.actions.contractors;

import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractor extends PicsActionSupport {
	public ContractorRegistrationRequest newContractor;
	protected ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	protected OperatorAccountDAO operatorAccountDAO;
	protected UserDAO userDAO;
	protected CountryDAO countryDAO;
	protected StateDAO stateDAO;
	private int requestID;
	
	public RequestNewContractor(ContractorRegistrationRequestDAO contractorRegistrationRequestDAO, OperatorAccountDAO operatorAccountDAO, UserDAO userDAO, CountryDAO countryDAO, StateDAO stateDAO) {
		this.contractorRegistrationRequestDAO = contractorRegistrationRequestDAO;
		this.operatorAccountDAO = operatorAccountDAO;
		this.userDAO = userDAO;
		this.countryDAO = countryDAO;
		this.stateDAO = stateDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if(requestID   > 0) {
			newContractor = contractorRegistrationRequestDAO.find(requestID);
		}
		else {
			newContractor = new ContractorRegistrationRequest();
		}
		
		if(newContractor.getDeadline() == null) {
			newContractor.setDeadline(DateBean.addMonths(new Date(), 3));
		}
		
		if(button != null) {
			if(button.equals("Save")) {
				if(Strings.isEmpty(newContractor.getName())) 
					addActionError("Please fill the contractor Name");
				if(Strings.isEmpty(newContractor.getContact()))
					addActionError("Please fill the Contact Name");
				if(newContractor.getRequestedBy() == null)
					addActionError("Please select the Requested By Account");
				if(newContractor.getRequestedByUser() == null || Strings.isEmpty(newContractor.getRequestedByUserOther()))
					addActionError("Please select the Requested User for the Account");
				if(newContractor.getCountry() == null)
					addActionError("Please select a Country");
				if (newContractor.getCountry().getIsoCode().equals("US") || newContractor.getCountry().getIsoCode().equals("CA")) {
					if (newContractor.getState() == null || Strings.isEmpty(newContractor.getState().getIsoCode())) {
						addActionError("Please select a State");
					}
				}
				if(getActionErrors().size() > 0) {
					return SUCCESS;
				}
			}
			if(button.equals("Close Account")) {
				newContractor.setOpen(false);
			}
			if(button.equals("Send Email") || 
					button.equals("Contacted By Phone")) {
				if(button.equals("Send Email")) {
					// Send the Email
				}	
				newContractor.setContactCount(newContractor.getContactCount()+1);
				newContractor.setLastContactedBy(new User(permissions.getUserId()));
				newContractor.setLastContactDate(new Date());
			}
			newContractor.setAuditColumns(permissions);
			contractorRegistrationRequestDAO.save(newContractor);
			
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
}
