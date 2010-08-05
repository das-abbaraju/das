package com.picsauditing.search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.report.oq.ReportOQ.Base;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class MainSearch extends PicsActionSupport implements Preparable {
	
	protected String searchTerm;
	
	protected int searchID = 0;
	protected String searchType = "";
	protected String accType = "";
	
	protected final String indexTable = "app_index";
	protected final String indexStats = "app_index_stats";
	
	protected String fullList = "";
	protected Database db = new Database();
	
	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private EmployeeDAO empDAO;
	
	public MainSearch(AccountDAO accountDAO, UserDAO userDAO, EmployeeDAO empDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.empDAO = empDAO;
	}
	
	@Override
	public void prepare() throws Exception {
		String[] qA = (String[])ActionContext.getContext().getParameters().get("q");
		if(qA!=null)
			searchTerm = qA[0];		
	}

	public String execute() throws SQLException, IOException{
		if("getResult".equals(button)){
			if(searchType.equals("account")){
				if("Contractor".equals(accType)){
					redirect("ContractorView.action?id="+searchID);
				} else if("Operator".equals(accType)){
					redirect("FacilitiesEdit.action?id="+searchID);
				} else if("Assessment".equals(accType)){
					redirect("AssessmentCenterEdit.action?id="+searchID);
				} else if("Corporate".equals(accType)){
					redirect("FacilitiesEdit.action?id="+searchID);
				}				
			} else if(searchType.equals("user")){
				redirect("UsersManage.action?id="+searchID);
			} else if(searchType.equals("employee")){
				redirect("ManageEmployees.action?employee.id="+searchID);
			}
			
		} else {
			String query = buildQuery(searchTerm);
			System.out.println("--query--");
			System.out.println(query);
			List<BasicDynaBean> queryList = null;	
			queryList = db.select(query, true);
			System.out.println(db.getAllRows());
			getResults(queryList);
			return BLANK;
		}
		return SUCCESS;
	}
	private void getResults(List<BasicDynaBean> queryList) {
		// TODO Auto-generated method stub 
		StringBuilder sb = new StringBuilder();
		for(BasicDynaBean bdb : queryList){
			String check = (String)bdb.get("indexType");
			if(check.equals("A")||check.equals("AS")||check.equals("C")||check.equals("CO")||check.equals("O")){ // account
				Account a = accountDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));	
				sb.append(a.getReturnType()).append('|').append(a.getType()).append('|').append(a.getId()).append('|').append(a.getName()).append('|')
					.append(a.getCity()).append(", ").append(a.getState()).append("\n");
			}else if(check.equals("U")){ // user
				User u = userDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(u.getReturnType()).append('|').append("User").append('|').append(u.getId()).append('|').append(u.getName()).append('|').append(u.getAccount().getName()).append("\n");
			} else if(check.equals("G")){ // user group
				User u = userDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));	
				sb.append(u.getReturnType()).append('|').append("User Group").append('|').append(u.getId()).append('|').append(u.getName()).append('|').append(u.getAccount().getName()).append("\n");
			} else if(check.equals("E")){ // employee
				Employee e = empDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(e.getReturnType()).append('|').append("Employee").append('|').append(e.getId()).append('|').append(e.getDisplayName()).append('|').append(e.getAccount().getName()).append("\n");
			}
		}
		output = sb.toString();
	}

	public static void main(String[] args){
//		MainSearch ms = new MainSearch();
//		ms.buildQuery("bob pet");
	}
	
	public String buildQuery(String check){
		SelectSQL sql = new SelectSQL(indexTable+" i1");
		if(check==null || check.isEmpty())
			return null;
		String[] sA = check.toUpperCase().split("\\s+|@");
		for(int i=0; i<sA.length; i++){
			sA[i] = sA[i].replaceAll("^(HTTP://)(W{3})|^(HTTP://)|^(W{3}.)|\\W", "").replaceAll("[^a-zA-Z0-9\\s]", "");
		}
		System.out.println(sA[0]);
		if(sA.length>1)
			sortSearchTerms(sA);
		System.out.println(sA[0]);
		
		StringBuilder sb = new StringBuilder();
		sb.append("i1.foreignKey,i1.indexType, min(t.total*(v1.total/i1.weight");
		//sql.addField("i1.foreignKey,i1.indexType, min(t.total*(v1.total/i1.weight");
		for(int i=1; i<sA.length; i++){
			sb.append("+");
			sb.append("v").append(i+1).append(".total/i").append(i+1).append(".weight");
		}
		sb.append(")) score, (i1.value = '").append(sA[0]).append("') * i1.weight m");
		sql.addField(sb.toString());
		sb.setLength(0);
		sb.append("JOIN ").append(indexStats).append(" t ON i1.indexType = t.indexType AND t.value IS NULL\n");
		sb.append("JOIN ").append(indexStats).append(" v1 ON v1.indexType IS NULL and i1.value = v1.value");
		sql.addJoin(sb.toString());
		sb.setLength(0);
		for(int i=1; i<sA.length; i++){
			sb.setLength(0);
			String vTerm = "v"+(i+1);
			String iTerm = "i"+(i+1);
			sb.append("JOIN ").append(indexTable).append(" ").append(iTerm).append(" ON i1.indexType = ").append(iTerm).append(".indexType");
			sb.append(" AND i1.foreignKey = ").append(iTerm).append(".foreignKey AND ").append(iTerm).append(".value LIKE '").append(sA[i]).append("%'\n");
			sb.append("JOIN ").append(indexStats).append(" ").append(vTerm).append(" ON ").append(vTerm).append(".indexType IS NULL ");
			sb.append(" AND ").append(iTerm).append(".value = ").append(vTerm).append(".value");
			sql.addJoin(sb.toString());
		}
		sql.addWhere("i1.value LIKE '"+sA[0]+"%'");
		sql.addGroupBy("i1.foreignKey, i1.indexType");
		sql.addOrderBy("m DESC, score, foreignKey");
		sql.setLimit(10);
		
		return sql.toString();
	}

	private void sortSearchTerms(String[] sA){
		String commonSql = "' term, SUM(total) t FROM "+indexStats+" WHERE value LIKE '";
		String commonEnd = " AND indexType IS NULL)";
		StringBuilder fullQuery = new StringBuilder();
		fullQuery.append("(SELECT '").append(sA[0]).append(commonSql).append(sA[0]).append("%'").append(commonEnd);
		for(int i=1; i<sA.length; i++){
			fullQuery.append("\nUnion\n");
			fullQuery.append("(SELECT '").append(sA[i]).append(commonSql).append(sA[i]).append("%'").append(commonEnd);
		}
		fullQuery.append("\nORDER BY t");
		List<BasicDynaBean> l = null;
		try {
			l = db.select(fullQuery.toString(), false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i=0; i<sA.length; i++){
			sA[i] = (String)l.get(i).get("term"); // TODO null check
		}
				
	}

	public String getDataList() {
		return fullList;
	}

	public int getSearchID() {
		return searchID;
	}

	public void setSearchID(int searchID) {
		this.searchID = searchID;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getAccType() {
		return accType;
	}

	public void setAccType(String accType) {
		this.accType = accType;
	}
}
