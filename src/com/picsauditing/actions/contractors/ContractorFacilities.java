package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.naming.NoPermissionException;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorFacilities extends ContractorActionSupport {

	private ContractorOperatorDAO contractorOperatorDAO;
	private OperatorAccountDAO operatorDao = null;
	private FacilityChanger facilityChanger = null;

	private String state = null;

	private OperatorAccount operator = null;

	private List<ContractorOperator> currentOperators = null;
	private List<OperatorAccount> searchResults = null;
	
	private String msg = null;

	public ContractorFacilities(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorAccountDAO operatorDao, FacilityChanger facilityChanger,
			ContractorOperatorDAO contractorOperatorDAO) {
		super(accountDao, auditDao);
		this.operatorDao = operatorDao;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.facilityChanger = facilityChanger;
		this.subHeading = "Facilities";
		this.noteCategory = NoteCategory.OperatorChanges;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		limitedView = true;
		findContractor();

		if (permissions.isOperator())
			throw new NoPermissionException("Operators can't view this page");

		if (permissions.isContractor() && permissions.getAdminID() == 0) {
			contractor.setViewedFacilities(new Date());
			accountDao.save(contractor);
		}
		
		if (button != null) {
			if (button.equals("search")) {

				String where = "";

				if (state != null && state.length() > 0) {
					where += "state = '" + Utilities.escapeQuotes(state) + "'";
				}

				if (operator != null && !Strings.isEmpty(operator.getName())) {
					if (where.length() > 0)
						where += " AND ";
					where += "name LIKE '%" + Utilities.escapeQuotes(operator.getName()) + "%'";
				}

				searchResults = new ArrayList<OperatorAccount>();
				currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
				for (OperatorAccount opToAdd : operatorDao.findWhere(false, where, permissions)) {
					boolean linked = false;
					for(ContractorOperator co : currentOperators) {
						if (co.getOperatorAccount().equals(opToAdd))
							linked = true;
					}
					if (!linked)
						searchResults.add(opToAdd);
				}
				
				return "search";
			}
			
			if (button.equals("load")) {
				currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
				return button;
			}
			
			if("request".equals(button)) {
				if (operator.getId() > 0 ) {
					contractor.setRequestedBy(operator);
					accountDao.save(contractor);
				}
				return SUCCESS;
			}
			
			if(button.equals("SwitchToTrialAccount")) {
				contractor.setAcceptsBids(true);
				contractor.setRenew(false);
				InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
				contractor.setNewMembershipLevel(fee);
				accountDao.save(contractor);
				return SUCCESS;
			}

			boolean recalculate = false;

			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(operator.getId());
			facilityChanger.setPermissions(permissions);

			if (button.equals("addOperator")) {
				facilityChanger.add();
				recalculate = true;
			}

			if (button.equals("removeOperator")) {
				facilityChanger.remove();
				recalculate = true;
			}

			if (recalculate) {
				findContractor();
				InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);

				contractor.setNewMembershipLevel(fee);
				accountDao.save(contractor);
			}
		}
		
		currentOperators = contractorOperatorDAO.findByContractor(id, permissions);

		return SUCCESS;
	}

	public TreeMap<String, String> getStateList() {
		return State.getStates(true);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	public List<ContractorOperator> getCurrentOperators() {
		return currentOperators;
	}
	
	public List<OperatorAccount> getSearchResults() {
		return searchResults;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public InvoiceFee getCurrentMembership() {
		InvoiceFee invoiceFee = BillingCalculatorSingle.calculateAnnualFeeForContractor(contractor, new InvoiceFee());
		InvoiceFeeDAO invoiceFeeDAO = (InvoiceFeeDAO) SpringUtils.getBean("InvoiceFeeDAO");
		return invoiceFeeDAO.find(invoiceFee.getId());
	}
	
	public boolean isTrialContractor() {
		if(!contractor.isActiveB() 
				&& contractor.isRenew() 
				&& contractor.getRequestedBy().isAcceptsBids()) {
			return true;
		}
		return false;
	}
}
