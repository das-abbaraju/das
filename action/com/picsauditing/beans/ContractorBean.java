package com.picsauditing.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;

import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.dao.GenericJPADAO;
import com.picsauditing.jpa.entities.ContractorInfoReport;
import com.picsauditing.jsf.utils.JSFListDataModel;


public class ContractorBean extends JSFListDataModel<ContractorInfoReport>{
	
	private static final int SORT_BY_NAME = 0;
	private Set<Integer> ajaxKeys = null;
	private String acctName = null;
	private int searchId = 0;
	private String searchBy = "";
	private boolean doSearch = false;
	private ContractorInfoReportDAO dao;
		
	@Override
	protected List<ContractorInfoReport> getList() {
		DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
		dao = daof.getContractorInfoReportDAO();		
		dao.setMax(getMaxResults());
		List<ContractorInfoReport> reports = null;
		if(!doSearch)
			reports = dao.executeNamedQuery("getActiveContractors", null);
			//reports = dao.findAll();
		else{
			if(searchBy.equals("Operator"))
				reports = SearchFacadeBean.getContractorsByOperator(dao, searchId);
			
			if(searchBy.equals("Auditor"))
				reports = SearchFacadeBean.getContractorsByAuditor(dao, searchId);
			
			if(searchBy.equals("Name"))
				reports = SearchFacadeBean.getContractorsByName(dao, acctName);
		}
		
		
		return reports;
		
		
	}

	@Override
	public String select() {
		ajaxKeys = new HashSet<Integer>();
		setSelectedItem((ContractorInfoReport)getDataModel().getRowData());		
        int rowKey = getDataModel().getRowIndex();
        ajaxKeys.add(rowKey);
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
		
		public String getSearchBy() {
			return searchBy;
		}

		public void setSearchBy(String searchBy) {
			this.searchBy = searchBy;
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
			  getDataModel().setWrappedData(null);
			  doSearch = false;
			  acctName = "";
			  setFirstResult(0);
			  searchBy = "";		  
		}
		
		public void search(ActionEvent event) {
			   getDataModel().setWrappedData(null);
			   doSearch = true;
			   setFirstResult(0);
		}

		public ContractorInfoReportDAO getDao() {
			return dao;
		}

		public void setDao(ContractorInfoReportDAO dao) {
			this.dao = dao;
		}

		public int getSearchId() {
			return searchId;
		}

		public void setSearchId(int searchId) {
			this.searchId = searchId;
		}		
	
}
