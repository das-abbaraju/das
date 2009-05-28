package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AccountNameDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorFormDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class FacilitiesEdit extends OperatorActionSupport implements Preparable {
	protected String type = "Operator";
	protected int[] facilities = new int[300];
	protected Set<OperatorAccount> relatedFacilities = null;
	protected int nameId;
	protected String name;
	protected Map<String, Integer> foreignKeys = new HashMap<String, Integer>();

	protected FacilitiesDAO facilitiesDAO;
	protected AccountNameDAO accountNameDAO;
	protected OperatorFormDAO formDAO;

	public FacilitiesEdit(OperatorAccountDAO operatorAccountDAO, FacilitiesDAO facilitiesDAO,
			AccountNameDAO accountNameDAO, OperatorFormDAO formDAO) {
		super(operatorAccountDAO);
		this.facilitiesDAO = facilitiesDAO;
		this.accountNameDAO = accountNameDAO;
		this.formDAO = formDAO;
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
			if (button.equalsIgnoreCase("RemoveName")) {
				accountNameDAO.remove(nameId);
				return SUCCESS;
			}

			if (button.equalsIgnoreCase("AddName")) {
				boolean skip = false;
				name = name.trim();

				for (AccountName an : operator.getNames()) {
					if (an.getName().equalsIgnoreCase(name)) {
						skip = true;
						name = "";
						break;
					}
				}
				if (!skip) {
					AccountName account = new AccountName();
					account.setAccount(new Account());
					account.getAccount().setId(id);
					account.setName(name);
					account.setAuditColumns(permissions);
					operator.getNames().add(account);
					Collections.sort(operator.getNames(), new Comparator<AccountName>() {
						@Override
						public int compare(AccountName o1, AccountName o2) {
							return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
						}
					});
					account = accountNameDAO.save(account);
					// operator.getNames().add(account);
				}
				return SUCCESS;
			}

			if (button.equalsIgnoreCase("Save")) {
				tryPermissions(OpPerms.ManageOperators, OpType.Edit);

				Map<Integer, OperatorAccount> opMap = new HashMap<Integer, OperatorAccount>();
				for(OperatorAccount op : getRelatedFacilities()) {
					opMap.put(op.getId(), op);
				}

				for(String key : foreignKeys.keySet()) {
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
				if (id == 0) {
					// Save so we can get the id and then update the NOLOAD with a unique id
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
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "active='Y'");
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
			// Add all my parents
			for (Facility parent : operator.getCorporateFacilities())
				relatedFacilities.add(parent.getCorporate());
			relatedFacilities.add(operator.getInheritAuditCategories());
			relatedFacilities.add(operator.getInheritAudits());
			relatedFacilities.add(operator.getInheritFlagCriteria());
			relatedFacilities.add(operator.getInheritInsuranceCriteria());
			relatedFacilities.add(operator.getInheritInsurance());
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

}
