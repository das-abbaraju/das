package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.users.UserAccountRole;
import com.picsauditing.dao.AccountUserDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
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

	protected FacilitiesDAO facilitiesDAO;
	protected OperatorFormDAO formDAO;
	protected AccountUserDAO accountUserDAO;
	protected UserDAO userDAO;
	protected Map<Integer, Integer> roleMap = new HashMap<Integer, Integer>();
	protected UserAccountRole accountRole;
	protected int userid = 0;
	protected int accountUserId;

	public FacilitiesEdit(OperatorAccountDAO operatorAccountDAO, FacilitiesDAO facilitiesDAO, OperatorFormDAO formDAO,
			AccountUserDAO accountUserDAO, UserDAO userDAO) {
		super(operatorAccountDAO);
		this.facilitiesDAO = facilitiesDAO;
		this.formDAO = formDAO;
		this.accountUserDAO = accountUserDAO;
		this.userDAO = userDAO;
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
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.ManageOperators);

		if (id == 0)
			subHeading = "Add " + type;

		if (button != null) {
			if (button.equalsIgnoreCase("Remove")) {
				if (accountUserId > 0) {
					accountUserDAO.remove(accountUserId);
				}
				return SUCCESS;
			}
			if (button.equalsIgnoreCase("Add Role")) {
				AccountUser accountUser = new AccountUser();
				accountUser.setAccount(operator);
				accountUser.setUser(new User(userid));
				accountUser.setRole(accountRole);
				accountUser.setAuditColumns();
				operator.getAccountUsers().add(accountUser);
				operatorDao.save(operator);
				int completePercent = 0;
				for (AccountUser accountUser2 : operator.getAccountUsers()) {
					if (accountUser2.getRole().equals(accountRole)) {
						completePercent += accountUser2.getOwnerPercent();
					}
				}
				if (completePercent != 100) {
					addActionMessage(accountRole.getDescription() + " is not 100 percent");
				}

				return SUCCESS;
			}

			if (button.equalsIgnoreCase("Save")) {
				tryPermissions(OpPerms.ManageOperators, OpType.Edit);

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

				Vector<String> errors = validateAccount(operator);
				if (errors.size() > 0) {
					for (String error : errors)
						addActionError(error);
					return SUCCESS;
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

				if (operator.getAccountUsers() != null) {
					for (AccountUser accountUser : operator.getAccountUsers()) {
						int userID = roleMap.get(accountUser.getId());
						accountUser.setUser(new User(userID));
					}
				}

				if (id == 0) {
					operator.setInheritAuditCategories(operator);
					operator.setInheritAudits(operator);
					operator.setInheritFlagCriteria(operator);
					operator.setInheritInsuranceCriteria(operator);
					operator.setInheritInsurance(operator);

					// Save so we can get the id and then update the NOLOAD with
					// a unique id
					operatorDao.save(operator);
				}
				operator.setQbListID("NOLOAD" + operator.getId());
				operator = operatorDao.save(operator);

				addActionMessage("Successfully saved " + operator.getName());
			} else {
				throw new Exception("no button action found called " + button);
			}
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
		return operatorDao.findWhere(false, "active='Y'");
	}

	public List<User> getUserList() throws Exception {
		return userDAO.findWhere("isActive='Yes' AND isGroup = 'No' AND account.id = 1100");
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

		if (!Utilities.isValidEmail(operator.getEmail()))
			errorMessages.addElement("Please enter a valid email address. "
					+ "This is our main way of communicating with you so it must be valid.");
		return errorMessages;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public Map<Integer, Integer> getRoleMap() {
		return roleMap;
	}

	public void setRoleMap(Map<Integer, Integer> roleMap) {
		this.roleMap = roleMap;
	}

	public UserAccountRole[] getRoleList() {
		return UserAccountRole.values();
	}

	public UserAccountRole getAccountRole() {
		return accountRole;
	}

	public void setAccountRole(UserAccountRole accountRole) {
		this.accountRole = accountRole;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getAccountUserId() {
		return accountUserId;
	}

	public void setAccountUserId(int accountUserId) {
		this.accountUserId = accountUserId;
	}
}
