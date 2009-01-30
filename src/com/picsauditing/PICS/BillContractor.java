package com.picsauditing.PICS;

import java.util.ArrayList;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;

public class BillContractor extends DataBean {
	public static int priceNoAudit = 99;
	public static int price1Op = 399;
	public static int price2Ops = 699;
	public static int price5Ops = 999;
	public static int price9Ops = 1299;
	public static int price13Ops = 1699;
	public static int priceFull = 1999;
	
	private ContractorBean cBean = new ContractorBean();
	
	public ContractorBean getContractor() {
		return cBean;
	}

	public void setSelectedFacilities(ArrayList<String> selectedFacilities) throws Exception {
		cBean.setFacilities(selectedFacilities);
	}
	
	/**
	 * Calculate the annual billing rate for contractors 
	 */
	public int calculatePrice() throws Exception {
		int facilityCount = countFacilities();
		
		// Contractors with no paying facilities are free
		if (facilityCount == 0) return 0;
		
		// If only one facility is selected and it's a "multiple" 
		// like Empire or BP Pipelines, then it's free too
		if (facilityCount==1 && cBean.getFacilities().get(0).doContractorsPay.equals("Multiple"))
			return 0;
		
		// if it doesn't require an audit then it's only $99
		cBean.isAudited(requiresAudit());
		if (cBean.isAudited()) {
			cBean.newBillingAmount = Integer.toString(priceNoAudit);
			return priceNoAudit;
		}
		// All others use the pricing matrix
		Integer newPrice = calculatePriceByFacilityCount(facilityCount);
		cBean.newBillingAmount = newPrice.toString();
		return newPrice;
	}
	
	////////////// Private Helper functions //////////////////////
	/*
	 * Return if the contractor requires an audit like 
	 * Office or Desktop from ANY operator
	 */
	private boolean requiresAudit() throws Exception {
		for (OperatorBean operator : cBean.getFacilities()) {
			if(requiresAuditForFacility(operator))
				return true;
		}
		return false;
	}
	
	/*
	 * Return if the contractor requires an audit like 
	 * Office or Desktop from this operator
	 * TODO: Make this more flexible so Office/Desktop/DA aren't hard coded. 
	 * We need to dynamically support IM or other audits in the future
	 */
	private boolean requiresAuditForFacility(OperatorBean operator) throws Exception {
		
		//if (cBean.riskLevel.equals("1")) // low risk facilities are exempt from audits
		//	return false;
		
		//if (operator.id.equals("4162") && cBean.riskLevel.equals("2"))
			// Sikorsky medium level risk don't require Office/Desktop
			//return false;
		
		for(AuditOperator cansee : operator.getCanSeeAudits()) {
			if (cansee.getMinRiskLevel() > 0) {
				if (cansee.getMinRiskLevel() <= Integer
						.parseInt(cBean.riskLevel)) {
					if (cansee.getAuditType().getId() == AuditType.DA
							&& "Yes".equals(cBean.oqEmployees))
						return true;
					if (cansee.getAuditType().getId() == AuditType.DESKTOP)
						return true;
					if (cansee.getAuditType().getId() == AuditType.OFFICE)
						return true;
				}
			}
		}
		
		return false;
	}

	/*
	 * Return the number of paying facilities
	 */
	private int countFacilities() throws Exception {
		Integer count = 0;
		for (OperatorBean operator : cBean.getFacilities()) {
			if(operator.doContractorsPay.equals("Yes") || operator.doContractorsPay.equals("Multiple"))
				// Add up all the facilities that are paying
				count++;
		}
		cBean.payingFacilities = count.toString();
		return count;
	}
	
	/**
	 * Return the price given the number of billable facilities
	 * @param facilities
	 * @return
	 * @throws Exception
	 */
	public static int calculatePriceByFacilityCount(int facilities) {
		if (facilities <= 0)
			return 0;
		if (facilities == 1)
			return price1Op;
		if (facilities <= 4)
			return price2Ops;
		if (facilities <= 8)
			return price5Ops;
		if (facilities <= 12)
			return price9Ops;
		if (facilities <= 19)
			return price13Ops;
		return priceFull;
	}

	public void setContractor(String conID) throws Exception {
		this.cBean = new ContractorBean();
		cBean.setFromDB(conID);
		cBean.setFacilitiesFromDB();
	}
	
	public void setContractor(ContractorBean cBean) throws Exception {
		this.cBean = cBean;
		// The facilities may already be there
		cBean.setFacilitiesFromDB();
	}

	public void writeToDB() throws Exception {
		this.cBean.writeBillingToDB();
	}
}
