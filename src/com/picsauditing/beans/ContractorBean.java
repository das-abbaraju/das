package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.dao.QueryMetaData;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorInfoReport;
import com.picsauditing.jsf.utils.JSFListDataModel;

@Transactional
public class ContractorBean extends JSFListDataModel<ContractorInfoReport>{
	
	private static final int SORT_BY_NAME = 0;
	private Set<Integer> ajaxKeys = null;
	private String acctName = "";
	private int auditorId = 0;
	private int operatorId = 0;
	private boolean doSearch = true;
	private ContractorInfoReportDAO dao;
	private Map<String, Integer> operators = null;
	private Map<String, Integer> auditors = null;
	private String dateStart = null;
	private boolean needsOshaVerification = true;
	private boolean needsEMRVerification = true;
	
		
	@Override
	protected List<ContractorInfoReport> getList() {
		
		//DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
		//dao = daof.getContractorInfoReportDAO();		
		dao = (ContractorInfoReportDAO) SpringJSFUtil.getSpringContext().getBean("ContractorInfoReportDAO");
		
		QueryMetaData qmd = new QueryMetaData();
		qmd.setMaxRows(getMaxResults());
		dao.setQueryMetaData(qmd);

		List<ContractorInfoReport> reports = null;;
		
		if(doSearch){
			if(acctName == "" && auditorId == 0 && operatorId == 0 && !needsOshaVerification && !needsEMRVerification)
				reports = dao.findActiveContractors();
			else if(acctName != "" || auditorId > 0 || operatorId > 0){
				
				needsOshaVerification = false;
				needsEMRVerification = false;

				reports = dao.findContractInfoReports(acctName, auditorId, operatorId );
				
			}else if(needsEMRVerification && !needsOshaVerification) {
				reports = dao.needsEmrVerification();
			}
			else {
				reports = dao.needsOshaAndEmrVerification();
			}
		}	
		
		return reports;
		
		
	}

	@Override
	public String select() {
		setSelectedItem((ContractorInfoReport)getDataModel().getRowData());	
       	return "success";
	}

	@Override
	protected List<ContractorInfoReport> sortList(List<ContractorInfoReport> list) {
		 List<ContractorInfoReport> out = new ArrayList<ContractorInfoReport>();
		  out.addAll(list);
		  switch (getSortBy()) {
	          case SORT_BY_NAME:
	               Collections.sort(out, isAscending() ? ASC_NAME_COMPARATOR : DESC_NAME_COMPARATOR);
	               break;
	        }
		  
		  return out;
	}
	
	 public String sortByName( ) {
	        return sort(SORT_BY_NAME);
	 }
	 
	 private static final Comparator<ContractorInfoReport> ASC_NAME_COMPARATOR = new Comparator<ContractorInfoReport>( ) {
	        public int compare(ContractorInfoReport a1, ContractorInfoReport a2) {
	            String s1 = a1.getAccount().getName();
	            String s2 = a2.getAccount().getName();
	            return s1.compareTo(s2);
	        }
	    };

	    private static final Comparator<ContractorInfoReport> DESC_NAME_COMPARATOR = new Comparator<ContractorInfoReport>( ) {
	        public int compare(ContractorInfoReport a1, ContractorInfoReport a2) {
	            String s1 = a1.getAccount().getName();
	            String s2 = a2.getAccount().getName();
	            return s2.compareTo(s1);
	        }
	    };

		public Set<Integer> getAjaxKeys() {
			return ajaxKeys;
		}

		public void setAjaxKeys(Set<Integer> ajaxKeys) {
			this.ajaxKeys = ajaxKeys;
		}
		
		public boolean isDoSearch() {
			return doSearch;
		}

		public void setDoSearch(boolean doSearch) {
			this.doSearch = doSearch;
		}	

		public String getAcctName() {
			return acctName;
		}

		public void setAcctName(String acctName) {
			this.acctName = acctName;
		}

		public void clear(ActionEvent event) {			  
			  refreshModel();
			  //dao.clear();
			  doSearch = false;
			  acctName = "";
			  setFirstResult(0);		  
		}		
		
		public void search(ActionEvent event){
			clearModel();
			//dao.clear();
			doSearch = true;		
					
		}		

		public ContractorInfoReportDAO getDao() {
			return dao;
		}

		public void setDao(ContractorInfoReportDAO dao) {
			this.dao = dao;
		}
		
		public int getAuditorId() {
			return auditorId;
		}

		public void setAuditorId(int auditorId) {
			this.auditorId = auditorId;
		}

		public int getOperatorId() {
			return operatorId;
		}

		public void setOperatorId(int operatorId) {
			this.operatorId = operatorId;
		}

		public Map<String, Integer> getOperators(){
			if(operators == null){
				//DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
				//AccountDAO adao = daof.getAccountDAO();
				List<Account> list =  SearchFacadeBean.getOperators();
				operators = new HashMap<String,Integer>();
				operators.put(" ", 0);
				for(Account a : list)
					operators.put(a.getName(), a.getId());
			}
			
			return operators;
				
			
		}
		
		public Map<String,Integer> getAuditors(){
			
			if(auditors == null){
				//DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
				//AccountDAO adao = daof.getAccountDAO();
				List<Account> list =  SearchFacadeBean.getAuditors();
				auditors = new HashMap<String,Integer>();
				auditors.put(" ", 0);
				for(Account a : list)
					auditors.put(a.getName(), a.getId());
			}
			
			return auditors;
			
		}
		
		public String refresh(){
			search(null);
			return "success";
		}
		
		@Override
		public int getCurrentYear(){
			int yr = super.getCurrentYear();
			String start = dateStart + "/" + String.valueOf(yr);
			try{
				if(!com.picsauditing.PICS.DateBean.isFirstBeforeSecond(com.picsauditing.PICS.DateBean.getTodaysDate(), start))
					yr++;
				
			}catch(Exception ex){
				
			}
			
			return yr;
		}

		public String getDateStart() {
			return dateStart;
		}

		public void setDateStart(String dateStart) {
			this.dateStart = dateStart;
		}

		public boolean isNeedsOshaVerification() {
			return needsOshaVerification;
		}

		public void setNeedsOshaVerification(boolean needsOshaVerification) {
			this.needsOshaVerification = needsOshaVerification;
		}

		public boolean isNeedsEMRVerification() {
			return needsEMRVerification;
		}

		public void setNeedsEMRVerification(boolean needsEMRVerification) {
			this.needsEMRVerification = needsEMRVerification;
		}	
		
				
}
