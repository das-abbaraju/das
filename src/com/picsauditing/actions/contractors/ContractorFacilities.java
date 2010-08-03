package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NoPermissionException;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorFacilities extends ContractorActionSupport {
	private int requestID;

	private ContractorOperatorDAO contractorOperatorDAO;
	private OperatorAccountDAO operatorDao = null;
	private FacilityChanger facilityChanger = null;

	private String state = null;

	private OperatorAccount operator = null;

	private List<ContractorOperator> currentOperators = null;
	private List<OperatorAccount> searchResults = null;

	private String msg = null;
	
	private ContractorType type = ContractorType.Onsite;

	public ContractorFacilities(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorAccountDAO operatorDao, FacilityChanger facilityChanger, ContractorOperatorDAO contractorOperatorDAO) {
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
		
		if (requestID > 0) {
			ContractorRegistrationRequestDAO crrDAO = 
				(ContractorRegistrationRequestDAO) SpringUtils.getBean("ContractorRegistrationRequestDAO");
			ContractorRegistrationRequest crr = crrDAO.find(requestID);
			contractor.setRequestedBy(crr.getRequestedBy());
			
			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(crr.getRequestedBy().getId());
			facilityChanger.setPermissions(permissions);
			facilityChanger.add();
			
			InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
			contractor.setNewMembershipLevel(fee);
			
			accountDao.save(contractor);
		}

		if (permissions.isOperator())
			throw new NoPermissionException("Operators can't view this page");

		if (permissions.isContractor()) {
			contractor.setViewedFacilities(new Date());
			accountDao.save(contractor);
		}

		if (contractor.getNonCorporateOperators().size() == 1) {
			contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
			accountDao.save(contractor);
		}
		if (button != null) {
			if (button.equals("search")) {
				if (!Strings.isEmpty(operator.getName()) || !Strings.isEmpty(state)) {
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
						for (ContractorOperator co : currentOperators) {
							if (co.getOperatorAccount().equals(opToAdd))
								linked = true;
						}
						if (!linked) {
							if (contractor.isOnsiteServices() && opToAdd.isOnsiteServices()
									|| contractor.isOffsiteServices() && opToAdd.isOffsiteServices()
									|| contractor.isMaterialSupplier() && opToAdd.isMaterialSupplier())
								searchResults.add(opToAdd);
						}
					}
				} else if (contractor.getOperators().size() == 0) {
					List<BasicDynaBean> data = SmartFacilitySuggest.getFirstFacility(contractor);

					searchResults = new ArrayList<OperatorAccount>();
					for (BasicDynaBean d : data) {
						OperatorAccount o = new OperatorAccount();
						
						if (d.get("onsiteServices").equals(1))
							o.setOnsiteServices(true);
						if (d.get("offsiteServices").equals(1))
							o.setOffsiteServices(true);
						if (d.get("materialSupplier").equals(1))
							o.setMaterialSupplier(true);
						
						o.setId(Integer.parseInt(d.get("opID").toString()));
						o.setName(d.get("name").toString());
						o.setStatus(AccountStatus.valueOf(d.get("status").toString()));
						
						if (contractor.isOnsiteServices() && o.isOnsiteServices()
								|| contractor.isOffsiteServices() && o.isOffsiteServices()
								|| contractor.isMaterialSupplier() && o.isMaterialSupplier())
							searchResults.add(o);
					}

					addActionMessage("This list of operators was generated based on your location. "
							+ "To find a specific operator, use the search filters above");
				} else {
					List<BasicDynaBean> data = SmartFacilitySuggest.getSimilarOperators(contractor, 10);

					searchResults = new ArrayList<OperatorAccount>();
					for (BasicDynaBean d : data) {
						OperatorAccount o = new OperatorAccount();
						
						if (d.get("onsiteServices").equals("1"))
							o.setOnsiteServices(true);
						if (d.get("offsiteServices").equals("1"))
							o.setOffsiteServices(true);
						if (d.get("materialSupplier").equals("1"))
							o.setMaterialSupplier(true);
						
						o.setId(Integer.parseInt(d.get("opID").toString()));
						o.setName(d.get("name").toString());
						o.setStatus(AccountStatus.valueOf(d.get("status").toString()));
						
						if (contractor.isOnsiteServices() && o.isOnsiteServices()
								|| contractor.isOffsiteServices() && o.isOffsiteServices()
								|| contractor.isMaterialSupplier() && o.isMaterialSupplier())
							searchResults.add(o);
					}

					addActionMessage("This list of operators is generated based on the operators you currently have selected."
							+ "To find a specific operator, use the search filters above");
				}

				return "search";
			}

			if (button.equals("load")) {
				currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
				return button;
			}

			if ("request".equals(button)) {
				if (operator.getId() > 0) {
					contractor.setRequestedBy(operator);
					if (contractor.isAcceptsBids() && !contractor.getRequestedBy().isAcceptsBids()) {
						contractor.setAcceptsBids(false);
						contractor.setRenew(true);
						InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
						contractor.setNewMembershipLevel(fee);
					}
					accountDao.save(contractor);
				}
				return SUCCESS;
			}

			if (button.equals("SwitchToTrialAccount")) {
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
				// Check to make sure the contractor's types match the one passed in
				if (type.equals(ContractorType.Onsite) && contractor.isOnsiteServices()
						|| type.equals(ContractorType.Offsite) && contractor.isOffsiteServices()
						|| type.equals(ContractorType.Supplier) && contractor.isMaterialSupplier()) {
					facilityChanger.setType(type);
					facilityChanger.add();
					recalculate = true;
				}
				else {
					// Not sure when this happens
					addActionError("The service you have selected for this operator doesn't match what " +
							"you selected for your company. Please choose another option.");
				}
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
	
	public int getRequestID() {
		return requestID;
	}
	
	public void setRequestID(int requestID) {
		this.requestID = requestID;
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
	
	public ContractorType getType() {
		return type;
	}
	
	public void setType(ContractorType type) {
		this.type = type;
	}

	public InvoiceFee getCurrentMembership() {
		InvoiceFee invoiceFee = BillingCalculatorSingle.calculateAnnualFeeForContractor(contractor, new InvoiceFee());
		InvoiceFeeDAO invoiceFeeDAO = (InvoiceFeeDAO) SpringUtils.getBean("InvoiceFeeDAO");
		return invoiceFeeDAO.find(invoiceFee.getId());
	}

	public boolean isTrialContractor() {
		if (contractor.getStatus().isPending() && contractor.getRequestedBy().isAcceptsBids()) {
			return true;
		}
		return false;
	}
	
	public int getTypeCount(OperatorAccount op) {
		int count = 0;
		
		if (contractor.isOnsiteServices() && op.isOnsiteServices())
			count++;
		if (contractor.isOffsiteServices() && op.isOffsiteServices())
			count++;
		if (contractor.isMaterialSupplier() && op.isMaterialSupplier())
			count++;
		
		return count;
	}
}
