package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountNameDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AccountName;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.Industry;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class FacilitiesEdit extends PicsActionSupport implements Preparable, ServletRequestAware {
	protected int opID;
	protected String type;
	protected OperatorAccount operatorAccount;
	protected OperatorAccountDAO operatorAccountDAO;
	protected FacilitiesDAO facilitiesDAO;
	protected AccountNameDAO accountNameDAO;
	protected int[] facilities = new int[300];
	protected Set<OperatorAccount> relatedFacilities = null;
	protected int auditorid;
	protected int nameId;
	protected int parentid;
	protected String name;
	protected HttpServletRequest request;

	public FacilitiesEdit(OperatorAccountDAO operatorAccountDAO, FacilitiesDAO facilitiesDAO,
			AccountNameDAO accountNameDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		this.facilitiesDAO = facilitiesDAO;
		this.accountNameDAO = accountNameDAO;
	}

	public void prepare() throws Exception {
		getPermissions();
		opID = getParameter("opID");
		type = request.getParameter("type");
		if (type == null)
			type = "Operator";

		if (opID > 0) {
			operatorAccount = operatorAccountDAO.find(opID);
			type = operatorAccount.getType();
			if (operatorAccount.getInsuranceAuditor() != null)
				auditorid = operatorAccount.getInsuranceAuditor().getId();
			int i = 0;
			for (Facility fac : operatorAccount.getOperatorFacilities()) {
				facilities[i] = fac.getOperator().getId();
				i++;
			}

			if (operatorAccount.getParent() != null)
				parentid = operatorAccount.getParent().getId();
			else if (operatorAccount.getCorporateFacilities().size() == 1) {
				operatorAccount.setParent(operatorAccount.getCorporateFacilities().get(0).getCorporate());
				operatorAccountDAO.save(operatorAccount);
				parentid = operatorAccount.getParent().getId();
			}
		}
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equalsIgnoreCase("RemoveName")) {
				accountNameDAO.remove(nameId);
				return SUCCESS;
			}

			if (button.equalsIgnoreCase("AddName")) {
				boolean skip = false;
				name = name.trim();

				for (AccountName an : operatorAccount.getNames()) {
					if (an.getName().equalsIgnoreCase(name)) {
						skip = true;
						name = "";
						break;
					}
				}
				if (!skip) {
					AccountName account = new AccountName();
					account.setAccount(new Account());
					account.getAccount().setId(opID);
					account.setName(name);
					account.setAuditColumns(new User(permissions.getUserId()));
					operatorAccount.getNames().add(account);
					Collections.sort(operatorAccount.getNames(), new Comparator<AccountName>() {
						@Override
						public int compare(AccountName o1, AccountName o2) {
							return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
						}
					});
					account = accountNameDAO.save(account);
					// operatorAccount.getNames().add(account);
				}
				return SUCCESS;
			}

			if (button.equalsIgnoreCase("Save")) {
				Vector<String> errors = validateAccount(operatorAccount);
				if (errors.size() > 0) {
					for (String error : errors)
						addActionError(error);
					return SUCCESS;
				}

				if (parentid > 0) {
					operatorAccount.setParent(operatorAccountDAO.find(parentid));
				} else {
					if (operatorAccount.getParent() != null) {
						parentid = operatorAccount.getParent().getId();
					}
				}

				if (operatorAccount.isCorporate()) {
					permissions.tryPermission(OpPerms.ManageCorporate, OpType.Edit);

					if (facilities != null) {
						List<OperatorAccount> newFacilities = new ArrayList<OperatorAccount>();

						for (int operatorID : facilities) {
							OperatorAccount opAccount = new OperatorAccount();
							opAccount.setId(operatorID);
							newFacilities.add(opAccount);
						}

						Iterator<Facility> facList = operatorAccount.getOperatorFacilities().iterator();
						while (facList.hasNext()) {
							Facility opFacilities = facList.next();
							if (newFacilities.contains(opFacilities.getOperator())) {
								newFacilities.remove(opFacilities.getOperator());
							} else {
								facilitiesDAO.remove(opFacilities);
								if (operatorAccount.equals(opFacilities.getOperator().getParent())) {
									opFacilities.getOperator().setParent(null);
									operatorAccountDAO.save(opFacilities.getOperator());
								}
								facList.remove();
							}
						}

						for (OperatorAccount opAccount : newFacilities) {
							opAccount = operatorAccountDAO.find(opAccount.getId());
							if (opAccount != null) {
								Facility facility = new Facility();
								facility.setCorporate(operatorAccount);
								facility.setOperator(opAccount);
								facilitiesDAO.save(facility);
								operatorAccount.getOperatorFacilities().add(facility);
								if (opAccount.getParent() == null) {
									opAccount.setParent(operatorAccount);
									operatorAccountDAO.save(opAccount);
								}
							}
						}
					}

				} else {
					permissions.tryPermission(OpPerms.ManageOperators, OpType.Edit);
				}
				operatorAccount.setType(type);
				if (auditorid > 0)
					operatorAccount.setInsuranceAuditor(new User(auditorid));
				operatorAccountDAO.save(operatorAccount);
				addActionMessage("Successfully modified " + operatorAccount.getName());
			} else {
				throw new Exception("no button action found called " + button);
			}
		}

		return SUCCESS;
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public OperatorAccount getOperator() {
		return operatorAccount;
	}

	public void setOperator(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
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

	public int getAuditorid() {
		return auditorid;
	}

	public void setAuditorid(int auditorid) {
		this.auditorid = auditorid;
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

	public int getParentid() {
		return parentid;
	}

	public void setParentid(int parentid) {
		this.parentid = parentid;
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
			relatedFacilities.add(operatorAccount);
			// Add all my parents
			for(Facility parent : operatorAccount.getCorporateFacilities())
				relatedFacilities.add(parent.getCorporate());
			relatedFacilities.add(operatorAccount.getInheritAuditCategories());
			relatedFacilities.add(operatorAccount.getInheritAudits());
			relatedFacilities.add(operatorAccount.getInheritFlagCriteria());
			relatedFacilities.add(operatorAccount.getInheritInsuranceCriteria());
			
		}
		return relatedFacilities;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public Vector<String> validateAccount(OperatorAccount operatorAccount) {
		Vector<String> errorMessages = new Vector<String>();
		if (null == type)
			errorMessages.addElement("Please indicate the account type");
		if (operatorAccount.getName().length() == 0)
			errorMessages.addElement("Please fill in the Company Name field");
		if (operatorAccount.getName().length() < 3)
			errorMessages.addElement("Your company name must be at least 3 characters long");

		if ((operatorAccount.getEmail().length() == 0) || (!Utilities.isValidEmail(operatorAccount.getEmail())))
			errorMessages
					.addElement("Please enter a valid email address. This is our main way of communicating with you so it must be valid.");
		return errorMessages;
	}

}
