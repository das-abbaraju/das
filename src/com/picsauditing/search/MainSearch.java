package com.picsauditing.search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.jpa.entities.State;
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
	
	private final int PAGEBREAK = 100;	

	protected final String indexTable = "app_index";
	protected final String indexStats = "app_index_stats";

	protected List<String> commonFilterSuggest = new ArrayList<String>();
	protected List<Indexable> fullList;
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
		String[] qA = (String[]) ActionContext.getContext().getParameters().get("q");
		if (qA != null)
			searchTerm = qA[0];
	}

	public String execute() throws SQLException, IOException {
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
		} else if ("search".equals(button)) {
			String query = buildQuery(searchTerm, startIndex, 100, true);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			String commonTermQuery = buildCommonTermQuery(searchTerm, totalRows);
			System.out.println(commonTermQuery);
			if (totalRows>PAGEBREAK) {
				List<BasicDynaBean> commonList = db.select(commonTermQuery, false);
				buildCommonSuggest(commonList, searchTerm);
			}
			buildPages();
			getFullResults(queryList);
			return SUCCESS;
		} else {
			String query = buildQuery(searchTerm, 0, 10, true);
			List<BasicDynaBean> queryList = db.select(query, true);
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

	private void getResults(List<BasicDynaBean> queryList) {
		StringBuilder sb = new StringBuilder();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			if (check.equals("A") || check.equals("AS") || check.equals("C") || check.equals("CO") || check.equals("O")) { // account
				Account a = accountDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(a.getSearchText());
			} else if (check.equals("U") || check.equals("G")) { // user
				User u = userDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(u.getSearchText());
			} else if (check.equals("E")) { // employee
				Employee e = empDAO.find(Integer.parseInt(bdb.get("foreignKey").toString()));
				sb.append(e.getSearchText());
			}
		}
		output = sb.toString();
	}

	private void getFullResults(List<BasicDynaBean> queryList) {
		fullList = new ArrayList<Indexable>();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int qID = Integer.parseInt(bdb.get("foreignKey").toString());
			if (check.equals("A") || check.equals("AS") || check.equals("C") || check.equals("CO") || check.equals("O")) { // account
				Account a = accountDAO.find(qID);
				fullList.add(a);
			} else if (check.equals("U") || check.equals("G")) { // user
				User u = userDAO.find(qID);
				fullList.add(u);
			} else if (check.equals("E")) { // employee
				Employee e = empDAO.find(qID);
				fullList.add(e);
			}
		}
	}

	private void buildPages() {
		pageLinks = LinkBuilder.getPageNOfXLinks(totalRows, PAGEBREAK, startIndex+1, (startIndex)+100, startIndex/100+1);
	}
	
	public String buildCommonTermQuery(String check, int total){
		String sub = buildQuery(check, null, null, false);
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
	 * 		If True then we build a query to grab the most common queries
	 * @return
	 * 		A string that is the query to run using db.select
	 */
	public String buildQuery(String check, Integer start, Integer limit, boolean isFull) {
		SelectSQL sql = new SelectSQL(indexTable + " i1");
		if(isFull)
			sql.setSQL_CALC_FOUND_ROWS(true);
		if (check == null || check.isEmpty())
			return null;
		String[] sA = buildTerm(check);
		System.out.println(sA[0]);
		if (sA.length > 1)
			sortSearchTerms(sA);
		StringBuilder sb = new StringBuilder();
		sb.append("i1.foreignKey");
		if(isFull){
			sb.append(",i1.indexType, min(t.total*(v1.total/i1.weight");
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
		sql.addWhere("i1.value LIKE '" + sA[0] + "%'");
		sql.addGroupBy("i1.foreignKey, i1.indexType");
		if(!isFull){
			sql.addOrderBy("foreignKey");
			return sql.toString();
		}
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
}
