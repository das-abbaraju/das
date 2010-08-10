package com.picsauditing.search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
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
import com.picsauditing.util.DoubleMap;
import com.picsauditing.util.Strings;

import edu.emory.mathcs.backport.java.util.Collections;

@SuppressWarnings("serial")
public class MainSearch extends PicsActionSupport implements Preparable {
	
	protected String searchTerm;
	
	protected int searchID = 0;
	protected int totalRows;
	protected String searchType = "";
	protected String accType = "";
	
	protected final String indexTable = "app_index";
	protected final String indexStats = "app_index_stats";
	
	protected String autoResults = "";
	protected List<String> fullList;
	protected List<BasicDynaBean> accountList;
	protected List<BasicDynaBean> employeeList;
	protected List<BasicDynaBean> userList;
	protected TreeSet<HashMap<BasicDynaBean, Float>> scoreSet;
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
				User u = userDAO.find(searchID);
				redirect("UsersManage.action?accountId="+u.getAccount().getId()+"&user.id="+searchID);
			} else if(searchType.equals("employee")){
				redirect("ManageEmployees.action?employee.id="+searchID);
			}			
			return BLANK;
		} else if("fullSearch".equals(button)){
			String query = buildQuery(searchTerm, false, "i1.indexType, m DESC, score, foreignKey");
			List<BasicDynaBean> queryList = db.select(query, true);
			getFullResults(queryList);
			return SUCCESS;			
		} else {
			String query = buildQuery(searchTerm, true, null);
			System.out.println("--query--");
			System.out.println(query);
			List<BasicDynaBean> queryList = db.select(query, true);
			System.out.println(db.getAllRows());
			getResults(queryList);
			return BLANK;
		}
	}
	@SuppressWarnings("unchecked")
	private void getFullResults(List<BasicDynaBean> queryList) {
		HashMap<BasicDynaBean, Float> scoreMap = new HashMap<BasicDynaBean, Float>();  // id, type, score
		for(BasicDynaBean bdb : queryList){ 
			String check = (String)bdb.get("indexType");
			int qID = Integer.parseInt(bdb.get("foreignKey").toString());
			float score = Float.parseFloat(bdb.get("score").toString());
			if(check.equals("A")||check.equals("AS")||check.equals("C")||check.equals("CO")||check.equals("O")){ // account
				scoreMap.put(getSingleResult("accounts a", "a.id, a.name", qID, null), score);
			}else if(check.equals("U") || check.equals("G")){ // user
				scoreMap.put(getSingleResult("users u", "u.id, u.name, a.name AS accName ", qID, "JOIN accounts a on u.accountID = a.id"), score);
			} else if(check.equals("E")){ // employee
				scoreMap.put(getSingleResult("employee e", "e.id, CONCAT(e.firstName,' ',e.lastName) AS name, a.name AS accName ", qID, "JOIN accounts a on e.accountID = a.id"), score);		
			}
		}
		scoreSet = new TreeSet(new Comparator() {
			public int compare(Object m, Object m1){
				return ((Comparable)((Map.Entry)m).getValue()).compareTo((Comparable)((Map.Entry)m1).getValue());
			}
		});
		scoreSet.addAll((Collection<? extends HashMap<BasicDynaBean, Float>>) scoreMap);
	}
	private List<BasicDynaBean> getList(String from, String fields, List<?> list, String join){
		Database db = new Database();
		List<BasicDynaBean> result = new ArrayList<BasicDynaBean>();
		SelectSQL sql = new SelectSQL(from);
		sql.addField(fields);
		sql.addWhere(from.substring(from.length()-1, from.length())+".id IN ("+Strings.implode(list)+")");
		sql.addOrderBy(from.substring(from.length()-1, from.length())+".id");
		if(join!=null)
			sql.addJoin(join);
		try {
			result = db.select(sql.toString(), false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	private BasicDynaBean getSingleResult(String from, String fields, int id, String join){
		Database db = new Database();
		List<BasicDynaBean> result = new ArrayList<BasicDynaBean>();
		SelectSQL sql = new SelectSQL(from);
		sql.addField(fields);
		sql.addWhere(from.substring(from.length()-1, from.length())+".id = "+id);
		sql.addOrderBy(from.substring(from.length()-1, from.length())+".id");
		if(join!=null)
			sql.addJoin(join);
		try {
			result = db.select(sql.toString(), false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result.get(0);
	}
	private void getResults(List<BasicDynaBean> queryList) {
		// TODO Auto-generated method stub 
		StringBuilder sb = new StringBuilder();
		for(BasicDynaBean bdb : queryList){
			String check = (String)bdb.get("indexType");
			if(check.equals("A")||check.equals("AS")||check.equals("C")||check.equals("CO")||check.equals("O")){ // account
				Account a = accountDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));	
				sb.append(a.getSearchText(false));
			}else if(check.equals("U") || check.equals("G")){ // user
				User u = userDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(u.getSearchText(false).toString());
			} else if(check.equals("E")){ // employee
				Employee e = empDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(e.getSearchText(false));
			}
		}
		output = sb.toString();
	}

	public static void main(String[] args){
//		MainSearch ms = new MainSearch();
//		ms.buildQuery("bob pet");
	}
	
	public String buildQuery(String check, boolean limit, String orderby){
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
		if(orderby == null)
			sql.addOrderBy("m DESC, score, foreignKey");
		else
			sql.addOrderBy(orderby);
		if(limit)
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
		return autoResults;
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

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public List<String> getFullList() {
		return fullList;
	}
	
	public void setFullList(List<String> accountsList) {
		this.fullList = accountsList;
	}

	public List<BasicDynaBean> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<BasicDynaBean> accountList) {
		this.accountList = accountList;
	}

	public List<BasicDynaBean> getEmployeeList() {
		return employeeList;
	}

	public void setEmployeeList(List<BasicDynaBean> employeeList) {
		this.employeeList = employeeList;
	}

	public List<BasicDynaBean> getUserList() {
		return userList;
	}

	public void setUserList(List<BasicDynaBean> userList) {
		this.userList = userList;
	}
}
