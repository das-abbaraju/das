package com.picsauditing.actions.contractors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;

public class ContractorFacilities extends ContractorActionSupport {

	private OperatorAccountDAO operatorDao = null;
	private FacilityChanger facilityChanger = null;
	
	private String state = null;
	private String corporate = null;
	
	private OperatorAccount operator = null;
	
	private List<OperatorAccount> searchResults = null;
	private Map<Integer, ContractorOperator> opMap = new HashMap<Integer, ContractorOperator>();
	
	
	public ContractorFacilities(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, OperatorAccountDAO operatorDao, FacilityChanger facilityChanger) {
		super( accountDao , auditDao );
		this.operatorDao = operatorDao;
		this.facilityChanger = facilityChanger;
		this.subHeading = "Facilities";
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		limitedView = true;
		findContractor();
		
		for( ContractorOperator co : contractor.getOperators() ) {
			opMap.put(co.getOperatorAccount().getId(), co);
		}
		
		if (permissions.isContractor() && permissions.getAdminID() == 0) {
			contractor.setViewedFacilities( new java.util.Date() );
			accountDao.save( contractor );
		}
		

		if( button != null ) {  
			
			if( button.equals("search")) {
				
				String where = " active = 'Y' ";
				
				if( state != null && state.length() > 0) {
					where += " and state = '" + state + "' ";
				}
				
				if( operator.getName() != null && operator.getName().length() > 0 ) {
					where += " and name like '%" + operator.getName() + "%' ";
				}
					
				if( corporate != null && corporate.length() > 0 ) {
					where += " and a in ( select operator from Facility where corporate.name like '%" + corporate+ "%') ";
				}
				
				searchResults = operatorDao.findWhere(false, where);
				return SUCCESS;
			}

			boolean recalculate = false;
			
			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(operator.getId());
			facilityChanger.setPermissions(permissions);
			
			if( button.equals("addOperator")) {
				facilityChanger.add();
				recalculate = true;				
			}
			
			if( button.equals("removeOperator")) {
			 	facilityChanger.remove();
				recalculate = true;
			}
			

			if( recalculate ) {
				findContractor();
				BillingCalculatorSingle calculator = new BillingCalculatorSingle();
				InvoiceFee fee = calculator.calculateAnnualFee(contractor);
				
				contractor.setNewMembershipLevel(fee);
				accountDao.save(contractor);
			}
			return SUCCESS;
		}
		
		
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

	public String getCorporate() {
		return corporate;
	}

	public void setCorporate(String corporate) {
		this.corporate = corporate;
	}

	public List<OperatorAccount> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(List<OperatorAccount> searchResults) {
		this.searchResults = searchResults;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}
	
	public Comparator<OperatorAccount> getOpComparator() {
		return new Comparator<OperatorAccount>() {
		@Override
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				if( o2 == null || o2.getName() == null ) return 1;
				if( o1 == null || o1.getName() == null) return -1;
				
				return o1.getName().compareTo(o2.getName());
			}	
		};
	}
	
	public Comparator<ContractorOperator> getConOpComparator() {
		
		final Comparator<OperatorAccount> opComparator;
		{
			opComparator = getOpComparator();
		}
		
		return new Comparator<ContractorOperator>() {
			@Override
			public int compare(ContractorOperator o1, ContractorOperator o2) {
				return opComparator.compare(o1.getOperatorAccount(), o2.getOperatorAccount());
			}	
		};
	}

	public Map<Integer, ContractorOperator> getOpMap() {
		return opMap;
	}

	public void setOpMap(Map<Integer, ContractorOperator> opMap) {
		this.opMap = opMap;
	}

	public boolean doCorporateCheck( ContractorOperator currentConOp ) {
		
		if( getUser().getAccount().isCorporate() ) {
			
			OperatorAccount op = (OperatorAccount) getUser().getAccount();
			
			for( Facility facility : op.getOperatorFacilities() ) {
				if( facility.getOperator().equals(currentConOp.getOperatorAccount())) {
					return true;
				}
			}
		}
		
		return  false;
	}
	
	
}
