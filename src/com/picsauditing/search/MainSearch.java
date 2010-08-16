package com.picsauditing.search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.LinkBuilder;

@SuppressWarnings("serial")
public class MainSearch extends PicsActionSupport implements Preparable {

	protected String searchTerm;

	protected int searchID = 0;
	protected int totalRows;
	protected int startIndex;
	protected String searchType = "";
	protected String accType = "";
	protected String pageLinks;
	protected String orderBy = null;
	
	private final int PAGEBREAK = 100;	

	protected final String indexTable = "app_index";
	protected final String indexStats = "app_index_stats";

	protected List<String> commonFilterSuggest = new ArrayList<String>();
	protected List<Indexable> fullList;
	protected Database db = new Database();
	protected Hashtable<Integer, Integer> ht;

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
		String[] qA = (String[]) ActionContext.getContext().getParameters().get("q");
		if (qA != null)
			searchTerm = qA[0];
	}

	public String execute() throws SQLException, IOException {
		if(!forceLogin())
			return LOGIN;
		
		if ("getResult".equals(button)) {
			if (searchType.equals("account")) {
				if ("Contractor".equals(accType)) {
					redirect("ContractorView.action?id=" + searchID);
				} else if ("Operator".equals(accType)) {
					redirect("FacilitiesEdit.action?id=" + searchID);
				} else if ("Assessment".equals(accType)) {
					redirect("AssessmentCenterEdit.action?id=" + searchID);
				} else if ("Corporate".equals(accType)) {
					redirect("FacilitiesEdit.action?id=" + searchID);
				}
			} else if (searchType.equals("user")) {
				User u = userDAO.find(searchID);
				redirect("UsersManage.action?accountId=" + u.getAccount().getId() + "&user.id=" + searchID);
			} else if (searchType.equals("employee")) {
				redirect("ManageEmployees.action?employee.id=" + searchID);
			}
			return BLANK;
		} else if ("search".equals(button)) { // full view and paging
			if(permissions.isCorporate()){
				String str = "SELECT gc.subID id FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID ="+permissions.getAccountId()+" GROUP BY id";
				List<BasicDynaBean> temp = db.select(str, false);
				ht = new Hashtable<Integer, Integer>(temp.size());
				for(BasicDynaBean bdb: temp){
					int value = Integer.parseInt(bdb.get("id").toString());
					ht.put(value, value);
				}
			}
			String query = buildQuery(searchTerm, startIndex, 100, false, true);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			String commonTermQuery = buildCommonTermQuery(searchTerm, totalRows);
			System.out.println(commonTermQuery);
			if (totalRows>PAGEBREAK) {
				List<BasicDynaBean> commonList = db.select(commonTermQuery, false);
				buildCommonSuggest(commonList, searchTerm);
			}
			fullList= getFullResults(queryList);
			if(fullList==null) 
				return SUCCESS;
			int end = 0;
			if(totalRows-(startIndex+1)<PAGEBREAK)
				end = totalRows;
			else end = startIndex + PAGEBREAK;
			buildPages(totalRows, startIndex+1, end, startIndex/100+1);
			return SUCCESS;
		} else { // autosuggest/complete
			String query = buildQuery(searchTerm, 0, 10, false, false);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			getResults(queryList);
			return BLANK;
		}
	}

	private void buildCommonSuggest(List<BasicDynaBean> commonList, String check) {
		String[] sA = buildTerm(check);
		for(BasicDynaBean bdb : commonList){
			String term = bdb.get("term").toString();
			for(String str : sA){
				if(term.equals(str))
					break;
			}
			commonFilterSuggest.add(term);
		}
	}
	private List<Indexable> getFullResults(List<BasicDynaBean> queryList) throws IOException{
		if(queryList.size()==1){ // only one result
			String type = queryList.get(0).get("indexType").toString();;
			int key = Integer.parseInt(queryList.get(0).get("foreignKey").toString());
			String url = "Search.action?button=getResult&searchID="+key+"&searchType=";
			if (type.equals("A") || type.equals("AS") || type.equals("C") || type.equals("CO") || type.equals("O")) { // account
				Account a = accountDAO.find(key);
				redirect(url+"account&accType="+a.getType());
			} else if (type.equals("U") || type.equals("G")) { // user
				redirect(url+"user");
			} else if (type.equals("E")) { // employee
				redirect(url+"employee");
			}
			return null;
		}
		List<Indexable> temp = new ArrayList<Indexable>();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int key = Integer.parseInt(bdb.get("foreignKey").toString());
			if (check.equals("A") || check.equals("AS") || check.equals("C") || check.equals("CO") || check.equals("O")) { // account
				Account a = accountDAO.find(key);
				temp.add(a);
			} else if (check.equals("U") || check.equals("G")) { // user
				User u = userDAO.find(key);
				temp.add(u);
			} else if (check.equals("E")) { // employee
				Employee e = empDAO.find(key);
				temp.add(e);
			}
		}		
		return temp;
	}

	private void getResults(List<BasicDynaBean> queryList) {
		StringBuilder sb = new StringBuilder();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int key = Integer.parseInt(bdb.get("foreignKey").toString());
			if (check.equals("A") || check.equals("AS") || check.equals("C") || check.equals("CO") || check.equals("O")) { // account
				Account a = accountDAO.find(key);
				sb.append(a.getSearchText());
			} else if (check.equals("U") || check.equals("G")) { // user
				User u = userDAO.find(key);
				sb.append(u.getSearchText());
			} else if (check.equals("E")) { // employee
				Employee e = empDAO.find(key);
				sb.append(e.getSearchText());
			}
		}
		output = sb.toString()+"FULL|Click to do a full search|"+searchTerm.replace(" ", "+");
		
	}
	
	public boolean checkCon(int id){
		return ht.containsValue(id);	
	}

	private void buildPages(int total, int start, int end, int page) {
		pageLinks = LinkBuilder.getPageNOfXLinks(total, PAGEBREAK, start, end, page);
		//pageLinks = LinkBuilder.getPageNOfXLinks(totalRows, PAGEBREAK, startIndex+1, (startIndex)+100, startIndex/100+1);
	}
	
	public String buildCommonTermQuery(String check, int total){
		String sub = buildQuery(check, null, null, true, true);
		StringBuilder cSb = new StringBuilder();
		cSb.append("SELECT a.value term, COUNT(a.value) cc FROM ").append(indexTable).append(" a JOIN (");
		cSb.append(sub).append(") AS r1 ON a.foreignKey = r1.foreignKey\nWHERE a.value NOT IN " +
				"(SELECT isoCode FROM ref_state)GROUP BY a.value HAVING cc/").append(total).append(" <.8\n")
			.append("ORDER BY cc DESC LIMIT 10");
		return cSb.toString();		
	}
	

	/**
	 * Builds the Query based on term and returns it as a string
	 * @param check
	 * 		Term to use to search for
	 * @param start
	 * 		Row to start at
	 * @param limit
	 * 		Limit for Search
	 * @param buildCommon
	 * 		If True then skip over total rows and various other parts of query
	 * @param fullSearch
	 * 		True for full search, false for 10 result ajax search
	 * @return
	 * 		A string that is the query to run using db.select
	 */
	public String buildQuery(String check, Integer start, Integer limit, boolean buildCommon, boolean fullSearch) {
		SelectSQL sql = new SelectSQL(indexTable + " i1");
		if(!buildCommon)
			sql.setSQL_CALC_FOUND_ROWS(true);
		if (check == null || check.isEmpty())
			return null;
		String[] sA = buildTerm(check);
		System.out.println(sA[0]);
		if (sA.length > 1)
			sortSearchTerms(sA);
		StringBuilder sb = new StringBuilder();
		if(permissions.isOperatorCorporate())
			sb.append("rName,");
		sb.append("i1.foreignKey");
		if(!buildCommon){
			sb.append(",i1.indexType, min(t.total*(v1.total/i1.weight"); // TODO change weight from / to *
			for (int i = 1; i < sA.length; i++) {
				sb.append("+");
				sb.append("v").append(i + 1).append(".total/i").append(i + 1).append(".weight");
			}
			sb.append(")) score, (i1.value = '").append(sA[0]).append("') * i1.weight m");
		}
		sql.addField(sb.toString());
		sb.setLength(0);
		sb.append("JOIN ").append(indexStats).append(" t ON i1.indexType = t.indexType AND t.value IS NULL\n");
		sb.append("JOIN ").append(indexStats).append(" v1 ON v1.indexType IS NULL and i1.value = v1.value");
		sql.addJoin(sb.toString());
		sb.setLength(0);
		for (int i = 1; i < sA.length; i++) {
			sb.setLength(0);
			String vTerm = "v" + (i + 1);
			String iTerm = "i" + (i + 1);
			sb.append("JOIN ").append(indexTable).append(" ").append(iTerm).append(" ON i1.indexType = ").append(iTerm)
					.append(".indexType");
			sb.append(" AND i1.foreignKey = ").append(iTerm).append(".foreignKey AND ").append(iTerm).append(
					".value LIKE '").append(sA[i]).append("%'\n");
			sb.append("JOIN ").append(indexStats).append(" ").append(vTerm).append(" ON ").append(vTerm).append(
					".indexType IS NULL ");
			sb.append(" AND ").append(iTerm).append(".value = ").append(vTerm).append(".value");
			sql.addJoin(sb.toString());
		}
		sb.setLength(0);
		if(permissions.isCorporate()){
			sb.append("\nJOIN ((\nSELECT a.name rName, a.id id, acc.rType FROM accounts a JOIN\n")
				.append("((SELECT f.opID id, 'O' rType FROM facilities f WHERE f.corporateID =").append(permissions.getAccountId()).append(')');
			sb.append("\nUNION\n").append("(SELECT a.id, IF(a.type = 'Corporate', 'CO', 'O') rType FROM accounts a JOIN operators o USING(id) WHERE o.parentID =")
				.append(permissions.getAccountId()).append(")) AS acc on a.id = acc.id\n)\n");
			// here
			if(fullSearch){
				sb.append("UNION\n(SELECT name rName, id, 'C' rType FROM accounts WHERE type = 'Contractor')\n");
			} else{
				sb.append("UNION\n(SELECT a.name rName, a.id, acc.rType FROM accounts a JOIN\n")
					.append("(SELECT gc.subID id, 'C' rType FROM generalcontractors gc\nJOIN facilities f ON f.opID = gc.genID AND f.corporateID =")
					.append(permissions.getAccountId()).append(" GROUP BY id) AS acc on a.id = acc.id)\n"); // here				
			}
			sb.append("UNION\n(SELECT u.name rName, u.id, IF(u.isGroup='Yes','G','U') rType FROM users u JOIN\n((select f.opID id FROM facilities f WHERE f.corporateID =")
				.append(permissions.getAccountId()).append(")\nUNION\n(SELECT o.id id FROM operators o WHERE o.parentID =").append(permissions.getAccountId())
				.append(")\n) AS t ON u.accountID = t.id)");
			sb.append("\nUNION\n(\nSELECT CONCAT(e.firstName, ' ', e.lastName) rName, e.id, 'E' rType FROM employee e join\n((SELECT f.opID id FROM facilities f WHERE f.corporateID =")
				.append(permissions.getAccountId()).append(")\nUNION\n(SELECT o.id id from operators o where o.parentID =").append(permissions.getAccountId()).append(")\n")
				.append("UNION\n(select gc.subID FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID =").append(permissions.getAccountId())
				.append(")\n) AS rE on e.accountID = rE.id)\n");
			sb.append(") AS r1\nON i1.foreignKey = r1.id AND i1.indexType = r1.rType");
			sql.addJoin(sb.toString());
			sb.setLength(0);
		} else if(permissions.isOperator()){
			sb.append("\nJOIN ((\nSELECT a.name rName, a.id, acc.rType FROM accounts a JOIN \n")
				.append("(SELECT gc.subID id, 'C' rType FROM generalcontractors gc WHERE gc.genID =").append(permissions.getAccountId()).append(") AS acc ON a.id = acc.id)");
			sb.append("\nUNION\n(SELECT u.name rName, u.id id, if(u.isGroup='Yes','G','U') rType FROM users u WHERE u.accountID =").append(permissions.getAccountId()).append(')');
			sb.append("\nUNION\n(SELECT CONCAT(e.firstName, ' ', e.lastName) rName, e.id, 'E' rType FROM employee e JOIN generalcontractors gc ON gc.subID = e.accountID WHERE gc.genID =")
				.append(permissions.getAccountId()).append(")\n) AS r1\nON i1.foreignKey = r1.id AND i1.indexType = r1.rType");
			sql.addJoin(sb.toString());
			sb.setLength(0);
		}
		sql.addWhere("i1.value LIKE '" + sA[0] + "%'");
		sql.addGroupBy("i1.foreignKey, i1.indexType");
		if(buildCommon){
			sql.addOrderBy("foreignKey");
			return sql.toString();
		}
		if(orderBy!=null)
			sql.addOrderBy("rName, m DESC, score, foreignKey");
		else
			sql.addOrderBy("m DESC, score, foreignKey");
		sql.setLimit(limit);
		sql.setStartRow(start);

		return sql.toString();
	}

	private String[] buildTerm(String check) {
		String[] sA = check.toUpperCase().split("\\s+|@");
		for (int i = 0; i < sA.length; i++) {
			sA[i] = sA[i].replaceAll("^(HTTP://)(W{3})|^(HTTP://)|^(W{3}.)|\\W", "").replaceAll("[^a-zA-Z0-9\\s]", "");
		}
		return sA;
	}

	private void sortSearchTerms(String[] sA) {
		String commonSql = "' term, SUM(total) t FROM " + indexStats + " WHERE value LIKE '";
		String commonEnd = " AND indexType IS NULL)";
		StringBuilder fullQuery = new StringBuilder();
		fullQuery.append("(SELECT '").append(sA[0]).append(commonSql).append(sA[0]).append("%'").append(commonEnd);
		for (int i = 1; i < sA.length; i++) {
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
		for (int i = 0; i < sA.length; i++) {
			sA[i] = (String) l.get(i).get("term"); 
		}

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

	public List<Indexable> getFullList() {
		return fullList;
	}

	public void setFullList(List<Indexable> fullList) {
		this.fullList = fullList;
	}

	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public String getPageLinks() {
		return pageLinks;
	}

	public void setPageLinks(String pageLinks) {
		this.pageLinks = pageLinks;
	}

	public List<String> getCommonFilterSuggest() {
		return commonFilterSuggest;
	}

	public void setCommonFilterSuggest(List<String> commonFilterSuggest) {
		this.commonFilterSuggest = commonFilterSuggest;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
}
