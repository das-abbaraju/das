package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class FacilitiesEdit extends OperatorActionSupport {
	@Autowired
	protected FacilitiesDAO facilitiesDAO;
	@Autowired
	protected OperatorFormDAO formDAO;
	@Autowired
	protected AccountUserDAO accountUserDAO;
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected UserSwitchDAO userSwitchDAO;
	@Autowired
	private InvoiceFeeDAO invoiceFeeDAO;

	protected String type = "Operator";
	protected List<Integer> facilities;
	protected Set<OperatorAccount> relatedFacilities = null;
	protected int nameId;
	protected String name;
	protected Map<UserAccountRole, List<AccountUser>> managers;

	protected int accountUserId;
	protected AccountUser salesRep = null;
	protected AccountUser accountRep = null;
	protected Country country;
	protected State state;
	protected int contactID;
	
	public List<OperatorAccount> operatorList;
	public List<OperatorAccount> childOperatorList;

	public String execute() throws Exception {
		if (permissions.isAdmin())
			tryPermissions(OpPerms.ManageOperators);
		else if (permissions.isContractor()) {
			throw new NoRightsException("Operator");
		} else if (permissions.isOperatorCorporate()) {
			if (operator == null)
				operator = operatorDao.find(permissions.getAccountId());

			facilities = new ArrayList<Integer>();
			for (Facility fac : operator.getOperatorFacilities()) {
				facilities.add(fac.getOperator().getId());
			}
		}

		if (operator != null) {
			type = operator.getType();
			subHeading = getText("FacilitiesEdit.Edit", new Object[] { getText("global." + type) });
		}

		if (operator == null && permissions.isAdmin()) {
			subHeading = "Add " + type;
			operator = new OperatorAccount();
			operator.setCountry(new Country("US"));
		} else {
			if (operator.getPrimaryContact() == null)
				addAlertMessage(getText("FacilitiesEdit.error.AddPrimaryContact"));
		}
		
		operatorList = getOperatorList();
		childOperatorList = operator.getChildOperators();
		
		return SUCCESS;
	}

	public String remove() {
		if (accountUserId > 0) {
			accountUserDAO.remove(accountUserId);
		}

		return SUCCESS;
	}

	public String addRole() {
		AccountUser accountUser = new AccountUser();
		if (accountRep.getUser().getId() > 0)
			accountUser = accountRep;
		else
			accountUser = salesRep;
		accountUser.setAccount(operator);
		// First of this month to next year, minus a day
		// Feb 1st, 2010 to Jan 31st, 2011
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		accountUser.setStartDate(calendar.getTime());
		if (accountUser.getRole() != null && accountUser.getRole().isAccountManager())
			calendar.add(Calendar.YEAR, 20);
		else {
			calendar.add(Calendar.YEAR, 1);
			calendar.add(Calendar.DATE, -1);
		}
		accountUser.setEndDate(calendar.getTime());
		accountUser.setAuditColumns(permissions);
		operator.getAccountUsers().add(accountUser);
		operatorDao.save(operator);
		int completePercent = 0;
		for (AccountUser accountUser2 : operator.getAccountUsers()) {
			if (accountUser2.getRole().equals(accountUser.getRole())) {
				completePercent += accountUser2.getOwnerPercent();
			}
		}
		if (completePercent != 100) {
			addActionMessage(accountUser.getRole().getDescription() + " is not 100 percent");
		}
		accountRep = null;
		salesRep = null;

		return SUCCESS;
	}

	public String copyToChildAccounts() {
		AccountUser accountUser = accountUserDAO.find(accountUserId);
		for (Facility facility : operator.getOperatorFacilities()) {
			if (facility.getOperator().getStatus().isActiveDemo()) {
				boolean hasAccountRep = false;
				int percent = 0;
				for (AccountUser accountUser2 : facility.getOperator().getAccountUsers()) {
					if (accountUser2.isCurrent() && accountUser2.getRole().equals(accountUser.getRole())) {
						percent += accountUser2.getOwnerPercent();
						if (accountUser2.getUser().equals(accountUser.getUser()) || percent >= 100) {
							hasAccountRep = true;
							break;
						}
					}
				}
				if (!hasAccountRep) {
					AccountUser au = new AccountUser();
					au.setUser(accountUser.getUser());
					au.setOwnerPercent(accountUser.getOwnerPercent());
					au.setRole(accountUser.getRole());
					au.setStartDate(accountUser.getStartDate());
					au.setEndDate(accountUser.getEndDate());
					au.setAccount(facility.getOperator());
					accountUserDAO.save(au);
				}
			}
		}
		addActionMessage("Successfully Copied to all child operators");

		return SUCCESS;
	}

	public String saveRole() {
		operatorDao.save(operator);

		return SUCCESS;
	}

	public String save() {
		if (facilities == null) {
			facilities = new ArrayList<Integer>();
			for (Facility fac : operator.getOperatorFacilities()) {
				facilities.add(fac.getOperator().getId());
			}
		}

		if (country != null && !country.equals(operator.getCountry()))
			operator.setCountry(country);
		if (state != null && !"".equals(state.getIsoCode()) && !state.equals(operator.getState()))
			operator.setState(state);

		Vector<String> errors = validateAccount(operator);
		if (errors.size() > 0) {
			operatorDao.clear();
			operator = operatorDao.find(operator.getId());
			for (Facility fac : operator.getOperatorFacilities()) {
				facilities.add(fac.getOperator().getId());
			}
			for (String error : errors)
				addActionError(error);
			return SUCCESS;
		}

		if (permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)) {

			if (operator.isCorporate()) {
				if (facilities != null) {
					List<OperatorAccount> newFacilities = new ArrayList<OperatorAccount>();

					for (int operatorID : facilities) {
						OperatorAccount opAccount = new OperatorAccount();
						opAccount.setId(operatorID);
						newFacilities.add(opAccount);
					}

					Iterator<Facility> facList = operator.getOperatorFacilities().iterator();
					while (facList.hasNext()) {
						Facility opFacilities = facList.next();
						if (newFacilities.contains(opFacilities.getOperator())) {
							newFacilities.remove(opFacilities.getOperator());
						} else {
							facilitiesDAO.remove(opFacilities);
							if (operator.equals(opFacilities.getOperator().getParent())) {
								opFacilities.getOperator().setParent(null);
								operatorDao.save(opFacilities.getOperator());
							}
							facList.remove();
						}
					}

					for (OperatorAccount opAccount : newFacilities) {
						opAccount = operatorDao.find(opAccount.getId());
						if (opAccount != null) {
							Facility facility = new Facility();
							facility.setCorporate(operator);
							facility.setOperator(opAccount);
							facility.setAuditColumns(permissions);
							facilitiesDAO.save(facility);
							operator.getOperatorFacilities().add(facility);
							if (opAccount.getParent() == null) {
								opAccount.setParent(operator);
								operatorDao.save(opAccount);
							}
						}
					}
					if (operator.getParent() != null && newFacilities.size() > 0)
						linkChildOperatorsToAllParentAccounts(newFacilities);
				}
			}

			operator.setType(type);
			operator.setAuditColumns(permissions);
			operator.setNaics(new Naics());
			operator.getNaics().setCode("0");
			operator.setNameIndex();

			if (operator.getId() == 0) {
				operator.setInheritFlagCriteria(operator);
				operator.setInheritInsuranceCriteria(operator);

				// Save so we can get the id and then update the NOLOAD
				// with
				// a unique id
				operatorDao.save(operator);
			}
		}
		// Automatically add/update the PICS corporate accounts
		addPicsConsortium();

		operator.setQbListID("NOLOAD" + operator.getId());
		operator.setQbListCAID("NOLOAD" + operator.getId());

		if (contactID > 0
				&& (operator.getPrimaryContact() == null || contactID != operator.getPrimaryContact().getId())) {
			operator.setPrimaryContact(userDAO.find(contactID));
		}

		// operator.setNeedsIndexing(true);
		operator = operatorDao.save(operator);
		id = operator.getId();
	

		addActionMessage(getText("FacilitiesEdit.SuccessfullySaved", new Object[] { operator.getName() }));

		return "redirect";
	}
	// Insure that all newly added facilities get linked to all parent accounts.
	// i.e.  if F1 -> Hub -> US -> Corporate
	// then F1 needs to be linked to US and Corporate
	private void linkChildOperatorsToAllParentAccounts(List<OperatorAccount> newFacilities) {
		List<OperatorAccount> parents = new ArrayList<OperatorAccount>();
		findParentAccounts(operator, parents);
		for (OperatorAccount child: newFacilities) {
			for (OperatorAccount parent: parents){
				// add the link into facilities, if it doesn't already exist.
				Facility facility = facilitiesDAO.findByCorpOp(parent.getId(), child.getId());
				if (facility == null) {
					facility = new Facility();
					facility.setCorporate(parent);
					facility.setOperator(child);
					facility.setAuditColumns(permissions);
					facilitiesDAO.save(facility);
				}
			}
		}
	}
	// Recursively find all the parents of this operator.
	private void findParentAccounts(OperatorAccount currentOperator, List<OperatorAccount> parents){
		if (currentOperator.getParent() == null)
			return;
		else {
			parents.add(currentOperator.getParent());
			findParentAccounts(currentOperator.getParent(), parents);
		}				
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		// find all operators
		operatorList = operatorDao.findWhere(false, "status IN ('Active','Demo','Pending')");
		
		// remove operators that are children of the current operator
		operatorList.removeAll(operator.getChildOperators());
		
		// return the list of operators not associated with the current operator
		return operatorList;
	}

	public List<User> getUserList() throws Exception {
		return userDAO.findByGroup(10801); // only showing users from marketing
		// group
	}

	public List<Integer> getFacilities() {
		return facilities;
	}

	public void setFacilities(List<Integer> facilities) {
		this.facilities = facilities;
	}

	public boolean isTypeOperator() {
		return "Operator".equals(type);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNameId() {
		return nameId;
	}

	public void setNameId(int nameId) {
		this.nameId = nameId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<OperatorAccount> getRelatedFacilities() {
		if (relatedFacilities == null) {
			relatedFacilities = new TreeSet<OperatorAccount>();
			// Add myself
			relatedFacilities.add(operator);
			if (operator.getId() > 0) {
				// Add all my parents
				if (operator.getCorporateFacilities() != null) {
					for (Facility parent : operator.getCorporateFacilities())
						relatedFacilities.add(parent.getCorporate());
				}
				if (operator.getInheritFlagCriteria() != null)
					relatedFacilities.add(operator.getInheritFlagCriteria());
				if (operator.getInheritInsuranceCriteria() != null)
					relatedFacilities.add(operator.getInheritInsuranceCriteria());
			}
		}

		return relatedFacilities;
	}

	public List<OperatorForm> getOperatorForms() {
		return formDAO.findByopID(this.id);
	}

	private Vector<String> validateAccount(OperatorAccount operator) {
		Vector<String> errorMessages = new Vector<String>();
		if (Strings.isEmpty(operator.getName()))
			errorMessages.addElement(getText("FacilitiesEdit.PleaseFillInCompanyName"));
		else if (operator.getName().length() < 2)
			errorMessages.addElement(getText("FacilitiesEdit.NameAtLeast2Chars"));

		if (operator.getCountry() == null) {
			errorMessages.addElement(getText("FacilitiesEdit.SelectCountry"));
		}

		if (operator.getActivationFee() != null && operator.getActivationFee() > 200) {
			errorMessages.addElement(getText("FacilitiesEdit.EnterValidRange"));
		}

		return errorMessages;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public UserAccountRole[] getRoleList() {
		return UserAccountRole.values();
	}

	public int getAccountUserId() {
		return accountUserId;
	}

	public void setAccountUserId(int accountUserId) {
		this.accountUserId = accountUserId;
	}

	public AccountUser getSalesRep() {
		return salesRep;
	}

	public void setSalesRep(AccountUser salesRep) {
		this.salesRep = salesRep;
	}

	public AccountUser getAccountRep() {
		return accountRep;
	}

	public void setAccountRep(AccountUser accountRep) {
		this.accountRep = accountRep;
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

	public List<User> getPrimaryOperatorContactUsers() {
		Set<User> primaryContactSet = new TreeSet<User>();

		primaryContactSet.addAll(userDAO.findByAccountID(operator.getId(), "Yes", "No"));

		// Include users that can switch to groups
		Set<User> groupSet = new HashSet<User>();
		groupSet.addAll(userDAO.findByAccountID(operator.getId(), "Yes", "Yes"));

		Set<User> switchToSet = new HashSet<User>();
		// Adding users that can switch to users on account
		for (User u : primaryContactSet)
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		// Adding users that can switch to groups on account
		for (User u : groupSet)
			switchToSet.addAll(userSwitchDAO.findUsersBySwitchToId(u.getId()));
		// Adding all SwitchTo users to primary contacts
		primaryContactSet.addAll(switchToSet);

		OperatorAccount parent = operator.getParent();
		// Moving up the level hierarchy and finding associated users.
		// Note: Cannot find users across same hierarchy level, only within
		// current hierarchy level and parents.
		while (parent != null) {
			primaryContactSet.addAll(userDAO.findByAccountID(parent.getId(), "Yes", "No"));
			parent = parent.getParent();
		}

		List<User> userList = new ArrayList<User>();
		userList.addAll(primaryContactSet);

		return userList;
	}

	public int getContactID() {
		return contactID;
	}

	public void setContactID(int contactID) {
		this.contactID = contactID;
	}

	public List<AccountUser> getAccountManagers() {
		List<AccountUser> list = new ArrayList<AccountUser>();

		for (AccountUser au : operator.getAccountUsers()) {
			if (au.isCurrent() && au.getRole().isAccountManager()) {
				list.add(au);
			}
		}

		return list;
	}

	public List<AccountUser> getSalesReps() {
		List<AccountUser> list = new ArrayList<AccountUser>();

		for (AccountUser au : operator.getAccountUsers()) {
			if (au.isCurrent() && au.getRole().isSalesRep()) {
				list.add(au);
			}
		}

		return list;
	}

	public Map<UserAccountRole, List<AccountUser>> getPreviousManagers() {
		if (managers == null) {
			List<AccountUser> aus = operator.getAccountUsers();
			List<AccountUser> ams = new ArrayList<AccountUser>();
			List<AccountUser> srs = new ArrayList<AccountUser>();

			for (AccountUser au : aus) {
				if (au.getEndDate().before(new Date())) {
					if (au.getRole().equals(UserAccountRole.PICSAccountRep))
						ams.add(au);
					else
						srs.add(au);
				}
			}

			managers = new HashMap<UserAccountRole, List<AccountUser>>();
			if (ams.size() > 0)
				managers.put(UserAccountRole.PICSAccountRep, ams);
			if (srs.size() > 0)
				managers.put(UserAccountRole.PICSSalesRep, srs);
		}

		return managers;
	}

	private void addPicsConsortium() {
		boolean picsGlobal = false;
		boolean picsCountrySpecific = false;

		for (Facility f : operator.getCorporateFacilities()) {
			if (f.getCorporate().getId() == 4)
				picsGlobal = true;

			if ((operator.getCountry().getIsoCode().equals("US") && f.getCorporate().getId() == 5)
					|| (operator.getCountry().getIsoCode().equals("CA") && f.getCorporate().getId() == 6)
					|| (operator.getCountry().getIsoCode().equals("AE") && f.getCorporate().getId() == 7))
				picsCountrySpecific = true;
		}

		if (!picsGlobal) {
			Facility f = new Facility();
			f.setCorporate(new OperatorAccount());
			f.getCorporate().setId(4);
			f.setAuditColumns(permissions);
			f.setOperator(operator);
			facilitiesDAO.save(f);
			operator.getCorporateFacilities().add(f);
		}

		if (!picsCountrySpecific) {
			// Clear out other country specific PICS Consortium accounts
			Iterator<Facility> iterator = operator.getCorporateFacilities().iterator();
			while (iterator.hasNext()) {
				Facility f = iterator.next();

				if (f.getCorporate().getId() >= 5 && f.getCorporate().getId() <= 7) {
					if ((operator.getCountry().getIsoCode().equals("US") && f.getCorporate().getId() != 5)
							|| (operator.getCountry().getIsoCode().equals("CA") && f.getCorporate().getId() != 6)
							|| (operator.getCountry().getIsoCode().equals("AE") && f.getCorporate().getId() != 7)) {
						iterator.remove();
						facilitiesDAO.remove(f);
					}
				}
			}

			Facility f = new Facility();
			f.setCorporate(new OperatorAccount());
			f.setAuditColumns(permissions);
			f.setOperator(operator);

			if (operator.getCountry().getIsoCode().equals("US"))
				f.getCorporate().setId(5);
			else if (operator.getCountry().getIsoCode().equals("CA"))
				f.getCorporate().setId(6);
			else if (operator.getCountry().getIsoCode().equals("AE"))
				f.getCorporate().setId(7);

			if (f.getCorporate().getId() > 0) {
				facilitiesDAO.save(f);
				operator.getCorporateFacilities().add(f);
			}
		}
	}

	public OperatorAccount getActivationFeeOperator() {
		InvoiceFee invoiceFee = invoiceFeeDAO.findByNumberOfOperatorsAndClass(FeeClass.Activation, 1);
		return operator.getActivationFeeOperator(invoiceFee);
	}
}
