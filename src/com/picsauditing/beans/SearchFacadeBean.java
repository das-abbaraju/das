package com.picsauditing.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.jpa.entities.ContractorInfoReport;


public class SearchFacadeBean {
		
	public static List<ContractorInfoReport> getActiveContractors() {
		ContractorInfoReportDAO dao = (ContractorInfoReportDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoReportDAO");
		return dao.findActiveContractors();
	}

	public static List getAuditors() 
	{
		AccountDAO d = (AccountDAO) SpringJSFUtil.getSpringContext().getBean("AccountDAO"); 
		return d.findAuditors();
	}
	
	public static List<ContractorInfoReport> getContractorsByAuditor(int id) {
		ContractorInfoReportDAO dao = (ContractorInfoReportDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoReportDAO");
		return dao.findContractorsByAuditor( id );
	}

	public static List<ContractorInfoReport> getContractorsByName(String acctName) {
		ContractorInfoReportDAO dao = (ContractorInfoReportDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoReportDAO");
		return dao.findContractorsByName( acctName );
	}

	public static List<ContractorInfoReport> getContractorsByOperator( int id) {
		ContractorInfoReportDAO dao = (ContractorInfoReportDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoReportDAO");
		return dao.findContractorsByOperator(id);		
		
	}

	public static List getOperators() {
		AccountDAO dao = (AccountDAO) SpringJSFUtil.getSpringContext().getBean("AccountDAO");
		return dao.findOperators();
		
	}
}
