package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.OperatorAccount;

@SuppressWarnings("serial")
public class CalculateFee extends PicsActionSupport {

	private OperatorAccountDAO operatorDao = null;
	private InvoiceFeeDAO feeDao = null;
	private int riskLevel = 0;
	private String oqEmployees = null;
	private String facilities = null;
	private InvoiceFee fee = null;

	public CalculateFee(OperatorAccountDAO operatorAccountDAO, InvoiceFeeDAO feeDao) {
		this.operatorDao = operatorAccountDAO;
		this.feeDao = feeDao;
	}

	@Override
	public String execute() throws Exception {

		if (button != null && "pricing".equals(button)) {

			ContractorAccount contractor = new ContractorAccount();
			contractor.setRiskLevel(LowMedHigh.getMap().get(riskLevel));
			// contractor.setOqEmployees(oqEmployees);

			List<Integer> selectedFacilities = new ArrayList<Integer>();
			String[] facilityArray = facilities.split(",");
			;
			for (String facility : facilityArray) {
				selectedFacilities.add(new Integer(facility));
			}

			List<OperatorAccount> operators = operatorDao.findOperators(selectedFacilities);

			for (OperatorAccount op : operators) {

				ContractorOperator co = new ContractorOperator();
				co.setContractorAccount(contractor);
				co.setOperatorAccount(op);
				contractor.getOperators().add(co);
			}

			fee = BillingCalculatorSingle.calculateAnnualFee(contractor);

			if (fee != null) {
				fee = feeDao.find(fee.getId());
			}
		}
		return SUCCESS;
	}

	public int getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(int riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getOqEmployees() {
		return oqEmployees;
	}

	public void setOqEmployees(String oqEmployees) {
		this.oqEmployees = oqEmployees;
	}

	public String getFacilities() {
		return facilities;
	}

	public void setFacilities(String facilities) {
		this.facilities = facilities;
	}

	public InvoiceFee getFee() {
		return fee;
	}

	public void setFee(InvoiceFee fee) {
		this.fee = fee;
	}
}
