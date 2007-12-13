package com.picsauditing.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UISelectOne;
import javax.faces.event.ActionEvent;

import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorInfoReportDAO;
import com.picsauditing.dao.DAOFactory;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorInfoReport;
import com.picsauditing.jsf.utils.JSFListDataModel;


public class ContractorBean extends JSFListDataModel<ContractorInfoReport>{
	
	private static final int SORT_BY_NAME = 0;
	private Set<Integer> ajaxKeys = null;
	private String acctName = "";
	private int auditorId = 0;
	private int operatorId = 0;
	private boolean doSearch = false;
	private ContractorInfoReportDAO dao;
	private Map<String, Integer> operators = null;
	private Map<String, Integer> auditors = null;
	
		
	@Override
	protected List<ContractorInfoReport> getList() {
		
		DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
		dao = daof.getContractorInfoReportDAO();		
		dao.setMax(getMaxResults());
		List<ContractorInfoReport> reports = null;
		String start = "select cr from ContractorInfoReport cr where ";
		String startCount = "select count(cr) from ContractorInfoReport cr where ";
		StringBuffer queryBuf = new StringBuffer();
		
		Map<String,Object>params = new HashMap<String,Object>();
		if(doSearch){
			if(acctName == "" && auditorId == 0 && operatorId == 0)
				reports = dao.executeNamedQuery("getActiveContractors", null);			
			else{
				if(!acctName.equals("")){
					params.put("name", acctName + "%");
					queryBuf.append("cr.account.name like :name AND ");
				}
				
				if(auditorId != 0){
					params.put("pqfAuditorId", auditorId);
					queryBuf.append("cr.pqfAuditorId=:pqfAuditorId AND ");
				}
				
				if(operatorId != 0){
					params.put("genId", operatorId );
					queryBuf.append(":genId in (select gc.id.genId from cr.generalContractors gc) AND ");
				}
				
				queryBuf.setLength(queryBuf.length()-5);
				StringBuffer query = queryBuf.insert(0, start);
				System.out.println("Query=" + query.toString());
				reports = dao.executeQuery(query.toString(), params);
			}
			
			//StringBuffer countBuf = queryBuf.insert(0, startCount);
			//countBuf.setLength(countBuf.length() - 7);
			//System.out.println("Query=" + countBuf.toString());
			//setCount((Long)dao.executeScalarQuery(countBuf.toString(), params));
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
			  clearModel();
			  doSearch = false;
			  acctName = "";
			  setFirstResult(0);			 
			  
		}		
		
		public void search(ActionEvent event){
			clearModel();
			doSearch = true;
			setFirstResult(0);			
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
				DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
				AccountDAO adao = daof.getAccountDAO();
				List<Account> list =  SearchFacadeBean.getOperators(adao);
				operators = new HashMap<String,Integer>();
				operators.put(" ", 0);
				for(Account a : list)
					operators.put(a.getName(), a.getId());
			}
			
			return operators;
				
			
		}
		
		public Map<String,Integer> getAuditors(){
			
			if(auditors == null){
				DAOFactory daof = DAOFactory.instance(DAOFactory.JPA, getPersistenceCtx());		
				AccountDAO adao = daof.getAccountDAO();
				List<Account> list =  SearchFacadeBean.getAuditors(adao);
				auditors = new HashMap<String,Integer>();
				auditors.put(" ", 0);
				for(Account a : list)
					auditors.put(a.getName(), a.getId());
			}
			
			return auditors;
			
		}
		
	
}
