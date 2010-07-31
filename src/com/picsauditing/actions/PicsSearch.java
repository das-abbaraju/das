package com.picsauditing.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.RowSetDynaClass;

import com.google.common.base.Strings;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.DBBean;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.PermissionQueryBuilderEmployee;
import com.picsauditing.util.PermissionQueryBuilderUser;

@SuppressWarnings("serial")
public class PicsSearch extends PicsActionSupport implements Preparable{
	
	private String searchTerm;
	private String searchText;
	private String seeAllText;
	
	private static final int DISPLAY_NUMBER = 10;
	
	public Map<SearchFields, String> possibleFields = new HashMap<SearchFields, String>();
	public Map<SearchTables, List<SearchFields>> possibleTables = new HashMap<SearchTables, List<SearchFields>>();
	public Map<SearchTables, String> results = new HashMap<SearchTables, String>(6);
	
	public List<BasicDynaBean> fullList = new ArrayList<BasicDynaBean>();
	
	public static final String BEGINS_WITH_NON_NUM_PHONE = "[^(^\\d|\\()].+";
	public static final String ALL_NUMS = "(\\d+)";
	public static final String NUM_NO_1_0_BEGIN = "[^(^1|0)](\\d+)";
	public static final String NUM_1_0_BEGIN = "^(1|0)(\\d+)";

	@Override
	public void prepare() throws Exception {
		searchTerm = (String)((String[])ActionContext.getContext().getParameters().get("q"))[0];		
		if(searchTerm==null)
			searchTerm = "";
	}
	
	public String execute() throws SQLException {
		if(!forceLogin())
			return LOGIN;
		
		System.out.println(searchTerm);
		DetermineFields(searchTerm);
		DetermineTables();
		BuildQueries();
		GetPreResults();
		return SUCCESS;
	}
	
	public static void main(String[] args) throws SQLException{
//		PicsSearch p = new PicsSearch();
//		p.DetermineFields("turner");
//		p.DetermineTables();
//		p.BuildQueries();	
		String s = "http://www.moowww.com";
		s=s.replaceAll("^(http://)(w{3})|^(w{3}.)|\\W", "");
		System.out.println(s);
		//p.GetPreResults();
		//System.out.println(d.select(p.results.get(SearchTables.Accounts), true));
	}
	
	private void GetPreResults() throws SQLException {
		
		Database db = new Database();
		
		List<BasicDynaBean> accountsList = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> opList = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> conList = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> empList = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> inList = new ArrayList<BasicDynaBean>();
		List<BasicDynaBean> usersList = new ArrayList<BasicDynaBean>();
		int acNum = 0, opNum = 0, coNum = 0, emNum = 0, inNum = 0, usNum = 0;
		int sum = 0;
		if(results.containsKey(SearchTables.Accounts)){
			accountsList = db.select(results.get(SearchTables.Accounts), false);
			acNum = accountsList.size();
			sum+=acNum;
		}
		/*if(results.containsKey(SearchTables.Op_Audit)){
			opList = runQuery(results.get(SearchTables.Op_Audit));		
			opNum = opList.size();
			sum+=opNum;
		}
		if(results.containsKey(SearchTables.Con_Audit)){
			conList = runQuery(results.get(SearchTables.Con_Audit));
			coNum = conList.size();
			sum+=coNum;
		}
		if(results.containsKey(SearchTables.Employee)){
			empList = runQuery(results.get(SearchTables.Employee));
			emNum = empList.size();
			sum+=emNum;
		}
		if(results.containsKey(SearchTables.Invoice)){
			inList = runQuery(results.get(SearchTables.Invoice));
			inNum = inList.size();
			sum+=inNum;
		}*/
		if(results.containsKey(SearchTables.Users)){
			usersList = db.select(results.get(SearchTables.Users), false);
			usNum = usersList.size();
			sum+=usNum;
		}
		if(sum>DISPLAY_NUMBER){
			int useA = Math.round(10*((float)acNum/sum));
			fullList.addAll(accountsList.subList(0, useA));
			int useU = Math.round(10*((float)usNum/sum));
			fullList.addAll(usersList.subList(0, useU));
			System.out.println("Total: "+sum+" | Size: "+fullList.size());
			seeAllText = "10 of "+sum+" results. Click to show All";
			
		} else{
			fullList.addAll(accountsList);
			fullList.addAll(usersList);
		}
	}

	public void addToTable(SearchTables sTable, SearchFields sField){
		List<SearchFields> l = possibleTables.get(sTable);
		if(l == null)
			possibleTables.put(sTable, l=new ArrayList<SearchFields>());
		l.add(sField);
	}
	
	public void DetermineTables() {
		if(possibleFields.containsKey(SearchFields.Id)){
			addToTable(SearchTables.Accounts, SearchFields.Id);
			addToTable(SearchTables.Con_Audit, SearchFields.Id);
			addToTable(SearchTables.Employee, SearchFields.Id);
			addToTable(SearchTables.Invoice, SearchFields.Id);	
			addToTable(SearchTables.Users, SearchFields.Id);			
		}
		if(possibleFields.containsKey(SearchFields.AccountName)){
			addToTable(SearchTables.Accounts, SearchFields.AccountName);			
		}
		if(possibleFields.containsKey(SearchFields.Name)){
			addToTable(SearchTables.Employee, SearchFields.Name);	
			addToTable(SearchTables.Employee, SearchFields.LastName);	
			addToTable(SearchTables.Users, SearchFields.Name);		
		}
		if(possibleFields.containsKey(SearchFields.NameIndex)){
			addToTable(SearchTables.Accounts, SearchFields.NameIndex);			
		}
		if(possibleFields.containsKey(SearchFields.Phone)){
			addToTable(SearchTables.Accounts, SearchFields.Phone);
			addToTable(SearchTables.Employee, SearchFields.Phone);	
			addToTable(SearchTables.Users, SearchFields.Phone);				
		}
		if(possibleFields.containsKey(SearchFields.Email)){
			addToTable(SearchTables.Accounts, SearchFields.Email);
			addToTable(SearchTables.Employee, SearchFields.Email);
			addToTable(SearchTables.Users, SearchFields.Email);			
		}
	}

	public void BuildQueries(){
		StringBuilder where = new StringBuilder();
		
		for(Map.Entry<SearchTables, List<SearchFields>> entry : possibleTables.entrySet()){
			where.setLength(0);
			SearchTables sTable = entry.getKey();
			List<SearchFields> sFields = entry.getValue();
			SelectSQL sql = new SelectSQL();
			int lastIndex = sFields.size()-1;	
			
			sql.addField(sTable.select);			
			sql.setFromTable(sTable.tblName+" "+sTable.tblAlias);
			for(int i=0; i<sFields.size()-1; i++){
				SearchFields fields = sFields.get(i);
				String s = possibleFields.get(fields);				
				where.append(sTable.tblAlias).append(".").append(fields).append(fields.find).append(s).append(" OR ");
			}
			String s = possibleFields.get(sFields.get(lastIndex));
			where.append(sTable.tblAlias).append(".").append(sFields.get(lastIndex)).append(sFields.get(lastIndex).find).append(s);
			
			sql.addWhere(where.toString());	
			if(sTable.equals(SearchTables.Users)){
				sql.addJoin("JOIN accounts a Using (id)");
			}
			String perm = buildPermission(sTable);
			
			String str1 = "ORDER BY "+sTable.tblAlias+"."+sFields.get(0).fieldName; // use first field as order by
			String query = sql.toString() + perm + str1;
			System.out.println(query);
			results.put(sTable, query);
		}
	}
	
	public String buildPermission(SearchTables sTable){
		String q = "";
		PermissionQueryBuilder pqb = null;
		if(sTable.equals(SearchTables.Accounts)){
			pqb = new PermissionQueryBuilder(permissions);	
			q+=" " + pqb.toString();
		}
		if(sTable.equals(SearchTables.Users)){
			pqb = new PermissionQueryBuilderUser(permissions);	
			q+=" " + pqb.toString();			
		}
		if(sTable.equals(SearchTables.Employee)){
			pqb = new PermissionQueryBuilderEmployee(permissions);
			q+=" " + pqb.toString();
		}
		return q;
		
	}

	public void DetermineFields(String str){ // builds the fields to check
		checkString(str);
		checkNumber(str);
	}
	
	/**
	 * Takes in a string and tries to determine if it's a name
	 * accountName, or email
	 * @param check
	 */
	public void checkString(String check){
		check = check.trim();
		String q = Utilities.escapeQuotes(check);
		
		if(check.matches("[a-zA-Z\\s*]+")){
			System.out.println("Account, Name");
			possibleFields.put(SearchFields.AccountName, "'"+q+"%'");
			possibleFields.put(SearchFields.Name, "'"+q+"%'");
			possibleFields.put(SearchFields.LastName, "'"+q+"%'");
			possibleFields.put(SearchFields.NameIndex, "'"+q+"%'"); // nameIndex logic here
			if(check.matches("\\s*")){
				System.out.println("Is Email");
				possibleFields.put(SearchFields.Email, "'"+q+"%'");
			}
			
		} else{
			if(check.contains("@")){
				System.out.println("Is Email");
				possibleFields.put(SearchFields.Email, "'"+q+"%'");
				return;
			}
			if(check.matches("[^\\s]+")){ // has no spaces
				System.out.println("Is Email");
				possibleFields.put(SearchFields.Email, "'"+q+"%'");
			}
			if(check.isEmpty())
				return;
			System.out.println("Account name");
			possibleFields.put(SearchFields.AccountName, "'"+q+"%'");
			possibleFields.put(SearchFields.NameIndex, "'"+q+"%'"); // nameIndex logic here				
		}
	}
	
	/**
	 * Takes in a string and determines if it is more like a phone
	 * number or an id, will populate fields based on this
	 * @param check
	 */
	public void checkNumber(String check){
		check = check.trim();
		// If a String begins with anything other than
		// a number or '(' then it is not a phone/id and we
		// can finish and check it for other fields
		if(check.matches(BEGINS_WITH_NON_NUM_PHONE)){
			return;
		}
		// If String is only numbers, then we can safely
		// Assume that it is either an id and/or a phone
		if(check.matches(ALL_NUMS)){
			// If less than 10, it's an id, else phone and id
			if(check.length()<=10){
				System.out.println("Id");
				possibleFields.put(SearchFields.Id, check);
				return;
			} else {
				System.out.println("ID or Phone");
				possibleFields.put(SearchFields.Id, check);
				possibleFields.put(SearchFields.Phone, check.substring(0, 10)); // phone logic
				return;
			}
		}
		// Still could be a phone number
		check = check.replaceAll("\\D", "");
		if(check.length()>=10){
			System.out.println("Phone");
			possibleFields.put(SearchFields.Phone, check.substring(0, 10)); // phone logic
		} 
	}

	public List<BasicDynaBean> getDataList() throws SQLException{
		return fullList;
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public void setSeeAllText(String seeAllText) {
		this.seeAllText = seeAllText;
	}

	public String getSeeAllText() {
		return seeAllText;
	}

}

enum SearchFields{
	//Field(column, match method, like or not)
	Id("id", false),
	AccountName("name", true),
	Name("name", true),
	LastName("lastName", true),
	NameIndex("nameIndex", true),
	Phone("phone", false),
	Email("email", true);
	
	String fieldName;
	String find;
	boolean like;
	
	private SearchFields(String fieldName, boolean like) {
		this.fieldName = fieldName;
		this.like = like;
		if(like)
			this.find = " LIKE ";
		else 
			this.find = " = ";
	}
	
	public String toString(){
		return this.fieldName;
	}
}

enum SearchTables{
	
	// Table(Table Name, Table Alias, Table Type, Select Fields)
	Accounts("accounts", "a", "\"accounts\" as rtype, a.id, a.type, a.name"),
	Con_Audit("contractor_audit", "con", "\"audit\" as rtype, con.id, con.auditTypeID"),
	Employee("employee", "e", "\"employee\" as rtype, e.id, e.firstName, e.lastName"),
	Invoice("invoice", "i", "\"invoice\" as rtype, i.id, i.accountID, i.status"),
	Users("users", "u", "\"users\" as rtype, u.id, u.accountID, u.name, a.name AS aName");
	
	String tblName;
	String tblAlias;
	String select;
	
	private SearchTables(String tblName, String tblAlias, String select){
		this.tblName = tblName;
		this.tblAlias = tblAlias;
		this.select = select;
	}
	
	public String toString(){
		return this.tblName;
	}
}