package com.picsauditing.beans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.dao.GeneralContractorDAO;
import com.picsauditing.jpa.entities.ContractorInfoReport;
import com.picsauditing.jpa.entities.GeneralContractor;


public class SearchFacadeBean {
		
	public static List<ContractorInfoReport> getActiveContractors(
			ContractorInfoReportDAO dao) {
		
		return dao.executeNamedQuery("getActiveContractors", null);
	}

	public static List getAuditors(AccountDAO dao) {
		return dao.executeNamedQuery("getAuditors", null);
	}
	
	public static List<ContractorInfoReport> getContractorsByAuditor(
			ContractorInfoReportDAO dao, int id) {
		
		Map<String,Object> params = new HashMap<String,Object>(); 
		params.put("pqfAuditorId", id);
		return dao.executeNamedQuery("contractorsByAuditor", params);
	}

	public static List<ContractorInfoReport> getContractorsByName(
			ContractorInfoReportDAO dao, String acctName) {
		
		Map<String,Object> params = new HashMap<String,Object>(); 
		params.put("name", acctName + "%");
		return dao.executeNamedQuery("contractorsByName", params);
	}

	public static List<ContractorInfoReport> getContractorsByOperator(
			ContractorInfoReportDAO dao, int id) {
		
		Map<String,Object> params = new HashMap<String,Object>(); 
		params.put("genId", id );
		return dao.executeNamedQuery("contractorsByOperator", params);		
		
	}

	public static List getOperators(AccountDAO dao) {
		return dao.executeNamedQuery("getOperators", null);
		
	}
	
	
}
