package com.picsauditing.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.IndexableDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.User;

@SuppressWarnings("serial")
public class MainSearch extends PicsActionSupport implements Preparable {
	
	protected String searchTerm;
	
	protected final String indexTable = "app_index";
	protected final String indexStats = "app_index_stats";
	
	protected List<String> fullList = new ArrayList<String>();
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
		searchTerm = (String)((String[])ActionContext.getContext().getParameters().get("q"))[0];		
		if(searchTerm==null)
			searchTerm = "";
	}

	public String execute(){
		String query = buildQuery(searchTerm);	
		List<BasicDynaBean> queryList = null;	
		try {
			queryList = db.select(query, true);
			System.out.println(db.getAllRows());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getResults(queryList);
		return SUCCESS;
	}
	private void getResults(List<BasicDynaBean> queryList) {
		// TODO Auto-generated method stub 
		IndexableDAO dao = new IndexableDAO();
		for(BasicDynaBean bdb : queryList){
			String str = "";
			String check = (String)bdb.get("indexType");
			if(check.equals("A")||check.equals("AS")||check.equals("C")||check.equals("CO")||check.equals("O")){ // account
				Account a = accountDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));	
				str = a.getType()+": "+a.getName()+" ("+a.getId()+")";
			}else if(check.equals("U")){
				User u = userDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				str ="User: "+u.getName()+u.getId();				
			} else if(check.equals("G")){
				User u = userDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				str ="User Group: "+u.getName()+" ("+u.getId()+")";				
			} else if(check.equals("E")){
				Employee e = empDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				str = "Employee: "+e.getDisplayName()+" ("+e.getId()+")";
			}
			fullList.add(str);
		}
		
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
		sb.append(")) score");
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
		sql.addOrderBy("score");
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

	public List<String> getDataList() {
		return fullList;
	}
}
