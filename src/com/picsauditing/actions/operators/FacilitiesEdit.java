package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Preparable;
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
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class FacilitiesEdit extends PicsActionSupport implements Preparable, ServletRequestAware {
	protected int opID;
	protected String type;
	protected OperatorAccount operatorAccount;
	protected OperatorAccountDAO operatorAccountDAO;
	protected FacilitiesDAO facilitiesDAO;
	protected AccountNameDAO accountNameDAO;
	protected int[] facilities = new int[300];
	protected int auditorid;
	protected int nameId;
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
			if(operatorAccount.getInsuranceAuditor() != null)
				auditorid = operatorAccount.getInsuranceAuditor().getId();
			int i = 0;
			for (Facility fac : operatorAccount.getOperatorFacilities()) {
				facilities[i] = fac.getOperator().getId();
				i++;
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
				AccountName account = new AccountName();
				account.setAccount(new Account());
				account.getAccount().setId(opID);
				account.setName(name);
				account.setAuditColumns(new User(permissions.getUserId()));
				account = accountNameDAO.save(account);
				return SUCCESS;
			}

			if (button.equalsIgnoreCase("Save")) {
				if (operatorAccount.isCorporate()) {
					permissions.tryPermission(OpPerms.ManageCorporate, OpType.Edit);
					operatorAccount.setIsCorporate(YesNo.Yes);
					
					if (facilities != null) {
						List<Integer> newFacilities = new ArrayList<Integer>();

						for (int operatorID : facilities) {
							newFacilities.add(Integer.valueOf(operatorID));
						}

						Iterator<Facility> facList = operatorAccount.getOperatorFacilities().iterator();
						while (facList.hasNext()) {
							Facility opFacilities = facList.next();
							if (newFacilities.contains(opFacilities.getOperator().getId())) {
								newFacilities.remove(opFacilities.getOperator().getId());
							} else {
								facilitiesDAO.remove(opFacilities);
								facList.remove();
							}
						}

						for (Integer newOpID : newFacilities) {
							Facility facility = new Facility();
							facility.setCorporate(operatorAccount);
							facility.setOperator(new OperatorAccount());
							facility.getOperator().setId(newOpID);
							facilitiesDAO.save(facility);
							operatorAccount.getOperatorFacilities().add(facility);
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

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
