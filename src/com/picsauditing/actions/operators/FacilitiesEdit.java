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

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.dao.UserSwitchDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class FacilitiesEdit extends OperatorActionSupport implements Preparable {
	protected String type = "Operator";
	protected int[] facilities = new int[300];
	protected Set<OperatorAccount> relatedFacilities = null;
	protected int nameId;
	protected String name;
	protected Map<String, Integer> foreignKeys = new HashMap<String, Integer>();
	protected Map<UserAccountRole, List<AccountUser>> managers;

	protected FacilitiesDAO facilitiesDAO;
	protected OperatorFormDAO formDAO;
	protected AccountUserDAO accountUserDAO;
	protected UserDAO userDAO;
	protected int accountUserId;
	protected AccountUser salesRep = null;
	protected AccountUser accountRep = null;
	protected Country country;
	protected State state;
	protected int contactID;
	protected UserSwitchDAO userSwitchDAO;

	public FacilitiesEdit(OperatorAccountDAO operatorAccountDAO, FacilitiesDAO facilitiesDAO, OperatorFormDAO formDAO,
			AccountUserDAO accountUserDAO, UserDAO userDAO, UserSwitchDAO userSwitchDAO) {
		super(operatorAccountDAO);
		this.facilitiesDAO = facilitiesDAO;
		this.formDAO = formDAO;
		this.accountUserDAO = accountUserDAO;
		this.userDAO = userDAO;
		this.userSwitchDAO = userSwitchDAO;
	}

	public void prepare() throws Exception {
		id = getParameter("id");

		if (id > 0) {
			findOperator();
			type = operator.getType();
			subHeading = "Edit " + operator.getType();

			int i = 0;
			for (Facility fac : operator.getOperatorFacilities()) {
				facilities[i] = fac.getOperator().getId();
				i++;
			}
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
			tryPermissions(OpPerms.ManageOperators);
		else if (permissions.isContractor()) {
			throw new NoRightsException("Operator");
		}
		
		if (button != null) {
			if (button.equalsIgnoreCase("Remove")) {
				if (accountUserId > 0) {
					accountUserDAO.remove(accountUserId);
				}
			}
			if (button.equalsIgnoreCase("Add Role")) {
				AccountUser accountUser = new AccountUser();
				if (accountRep.getUser().getId() > 0)
					accountUser = accountRep;
				else
					accountUser = salesRep;
				accountUser.setAccount(operator);
				accountUser.setStartDate(new Date());
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.YEAR, 20);
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
			}

			if (button.equalsIgnoreCase("Save Role")) {
				operatorDao.save(operator);
			}

			if (button.equalsIgnoreCase("Save")) {
				if (country != null && !country.equals(operator.getCountry()))
					operator.setCountry(country);
				if (state != null && !"".equals(state.getIsoCode()) && !state.equals(operator.getState()))
					operator.setState(state);

				Vector<String> errors = validateAccount(operator);
				if (errors.size() > 0) {
					operatorDao.clear();
					for (String error : errors)
						addActionError(error);
					return SUCCESS;
				}

				if (permissions.hasPermission(OpPerms.ManageOperators, OpType.Edit)) {

					Map<Integer, OperatorAccount> opMap = new HashMap<Integer, OperatorAccount>();
					for (OperatorAccount op : getRelatedFacilities()) {
						opMap.put(op.getId(), op);
					}

					for (String key : foreignKeys.keySet()) {
						int keyID = foreignKeys.get(key);

						if (key.equals("parent")) {
							operator.setParent(opMap.get(keyID));
						}
						if (key.equals("inheritFlagCriteria")) {
							operator.setInheritFlagCriteria(opMap.get(keyID));
						}
						if (key.equals("inheritInsuranceCriteria")) {
							operator.setInheritInsuranceCriteria(opMap.get(keyID));
						}
						if (key.equals("inheritInsurance")) {
							operator.setInheritInsurance(opMap.get(keyID));
						}
						if (key.equals("inheritAudits")) {
							operator.setInheritAudits(opMap.get(keyID));
						}
						if (key.equals("inheritAuditCategories")) {
							operator.setInheritAuditCategories(opMap.get(keyID));
						}
					}

					if (operator.isCorporate()) {
						permissions.tryPermission(OpPerms.ManageCorporate, OpType.Edit);

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
						}

					} else {
						permissions.tryPermission(OpPerms.ManageOperators, OpType.Edit);
					}
					operator.setType(type);
					operator.setAuditColumns(permissions);
					operator.setNaics(new Naics());
					operator.getNaics().setCode("0");
					operator.setNameIndex();

					if (id == 0) {
						operator.setInheritAuditCategories(operator);
						operator.setInheritAudits(operator);
						operator.setInheritFlagCriteria(operator);
						operator.setInheritInsuranceCriteria(operator);
						operator.setInheritInsurance(operator);

						// Save so we can get the id and then update the NOLOAD
						// with
						// a unique id
						operatorDao.save(operator);
					}
				}
				operator.setQbListID("NOLOAD" + operator.getId());

				if (contactID > 0
						&& (operator.getPrimaryContact() == null || contactID != operator.getPrimaryContact().getId())) {
					operator.setPrimaryContact(userDAO.find(contactID));
				}

				operator = operatorDao.save(operator);
				id = operator.getId();

				addActionMessage("Successfully saved " + operator.getName());
			}
		}

		if (id == 0)
			subHeading = "Add " + type;
		else {
			if (operator.getPrimaryContact() == null)
				addActionError("Please add a primary contact to this account");
		}

		return SUCCESS;
	}

	public Map<String, Integer> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(Map<String, Integer> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

	public Industry[] getIndustryList() {
		return Industry.values();
	}

	public List<OperatorAccount> getOperatorList() throws Exception {
		return operatorDao.findWhere(false, "status IN ('Active','Demo','Pending')");
	}

	public List<User> getUserList() throws Exception {
		return userDAO.findByGroup(10801); // only showing users from marketing group
	}

	public int[] getFacilities() {
		return facilities;
	}

	public void setFacilities(int[] facilities) {
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
				if (operator.getCorporateFacilities() != null)
					for (Facility parent : operator.getCorporateFacilities())
						relatedFacilities.add(parent.getCorporate());
				relatedFacilities.add(operator.getInheritAuditCategories());
				relatedFacilities.add(operator.getInheritAudits());
				relatedFacilities.add(operator.getInheritFlagCriteria());
				relatedFacilities.add(operator.getInheritInsuranceCriteria());
				relatedFacilities.add(operator.getInheritInsurance());
			}
		}

		return relatedFacilities;
	}

	public List<OperatorForm> getOperatorForms() {
		return formDAO.findByopID(this.id);
	}

	static private Vector<String> validateAccount(OperatorAccount operator) {
		Vector<String> errorMessages = new Vector<String>();
		if (Strings.isEmpty(operator.getName()))
			errorMessages.addElement("Please fill in the Company Name field");
		else if (operator.getName().length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long");

		if (operator.getCountry() == null) {
			errorMessages.addElement("Please select a country");
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
	
	public Map<UserAccountRole, List<String>> getAccountManagers() {
		List<AccountUser> aus = operator.getAccountUsers();
		List<String> ams = new ArrayList<String>();
		List<String> srs = new ArrayList<String>();
		
		for (AccountUser au : aus) {
			if (au.getRole().equals(UserAccountRole.PICSAccountRep))
				ams.add(au.getUser().getName());
			else
				srs.add(au.getUser().getName());
		}
		
		Map<UserAccountRole, List<String>> managers = new HashMap<UserAccountRole, List<String>>();
		if (ams.size() > 0)
			managers.put(UserAccountRole.PICSAccountRep, ams);
		if (srs.size() > 0)
			managers.put(UserAccountRole.PICSSalesRep, srs);
		
		return managers;
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
	
	public boolean isCurrent(Date startDate, Date endDate) {
		Date now = new Date();
		if (now.after(startDate) && now.before(endDate))
			return true;
		
		return false;
	}
}
