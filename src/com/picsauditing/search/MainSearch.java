package com.picsauditing.search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
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

	private final int PAGEBREAK = 100;

	protected List<Indexable> fullList;
	protected Hashtable<Integer, Integer> ht;

	protected Database db = new Database();
	protected SearchEngine searchEngine = null;

	private AccountDAO accountDAO;
	private UserDAO userDAO;
	private EmployeeDAO empDAO;

	public MainSearch(AccountDAO accountDAO, UserDAO userDAO, EmployeeDAO empDAO) {
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.empDAO = empDAO;
	}

	public MainSearch() {
	}

	@Override
	public void prepare() throws Exception {
		String[] qA = (String[]) ActionContext.getContext().getParameters()
				.get("q");
		if (qA != null)
			searchTerm = qA[0];
	}

	public String execute() throws SQLException, IOException {
		if (!forceLogin())
			return LOGIN;
		searchEngine = new SearchEngine(permissions);
		if ("getResult".equals(button)) { // pull up a result
			if (searchType.equals("account")) {
				if ("Contractor".equals(accType))
					redirect("ContractorView.action?id=" + searchID);
				else if ("Operator".equals(accType))
					redirect("FacilitiesEdit.action?id=" + searchID);
				else if ("Assessment".equals(accType))
					redirect("AssessmentCenterEdit.action?id=" + searchID);
				else if ("Corporate".equals(accType))
					redirect("FacilitiesEdit.action?id=" + searchID);
			} else if (searchType.equals("user")) {
				User u = userDAO.find(searchID);
				redirect("UsersManage.action?accountId="
						+ u.getAccount().getId() + "&user.id=" + searchID);
			} else if (searchType.equals("employee"))
				redirect("ManageEmployees.action?employee.id=" + searchID);

			return BLANK;
		} else if ("search".equals(button)) { // full view and paging

			List<String> terms = searchEngine
					.buildTerm(searchTerm, true, false);
			// if corporate then build list of contractors in their system
			ht = searchEngine.getConIds(permissions);
			String query = searchEngine.buildQuery(permissions, terms, null,
					startIndex, 100, false, true);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			String commonTermQuery = searchEngine.buildCommonTermQuery(terms,
					totalRows);

			if (totalRows > PAGEBREAK) {
				List<BasicDynaBean> commonList = db.select(commonTermQuery,
						false);
				searchEngine.buildCommonSuggest(commonList, searchTerm);
			}

			if (queryList != null && queryList.size() > 0)
				fullList = getFullResults(queryList);
			else {
				queryList = db.select(searchEngine.buildAccountSearch(
						permissions, terms), true);
				fullList = getFullResults(queryList);
			}
			if (fullList == null)
				return SUCCESS;

			int end = 0;
			if (totalRows - (startIndex + 1) < PAGEBREAK)
				end = totalRows;
			else
				end = startIndex + PAGEBREAK;
			buildPages(totalRows, startIndex + 1, end, startIndex / 100 + 1);

			return SUCCESS;
		} else { // autosuggest/complete
			List<String> terms = searchEngine
					.buildTerm(searchTerm, true, false);
			String query = searchEngine.buildQuery(permissions, terms, null, 0,
					10, false, false);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			if (queryList != null && queryList.size() > 0)
				getResults(queryList);
			else {
				queryList = db.select(searchEngine.buildAccountSearch(
						permissions, terms), true);
				getResults(queryList);
			}

			return BLANK;
		}
	}

	private List<Indexable> getFullResults(List<BasicDynaBean> queryList)
			throws IOException {
		String type = "";
		List<Indexable> temp = new ArrayList<Indexable>();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int key = Integer.parseInt(bdb.get("foreignKey").toString());
			if (check.equals("A") || check.equals("AS") || check.equals("C")
					|| check.equals("CO") || check.equals("O")) {
				Account a = accountDAO.find(key);
				type = "account&accType=" + a.getType();
				temp.add(a);
			} else if (check.equals("U") || check.equals("G")) {
				User u = userDAO.find(key);
				type = "user";
				temp.add(u);
			} else if (check.equals("E")) {
				Employee e = empDAO.find(key);
				type = "employee";
				temp.add(e);
			}
		}
		if (temp.size() == 1) {
			redirect("Search.action?button=getResult&searchID="
					+ temp.get(0).getId() + "&searchType=" + type);
		}
		return temp;
	}

	private void getResults(List<BasicDynaBean> queryList) {
		StringBuilder sb = new StringBuilder();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int key = Integer.parseInt(bdb.get("foreignKey").toString());
			if (check.equals("A") || check.equals("AS") || check.equals("C")
					|| check.equals("CO") || check.equals("O")) {
				Account a = accountDAO.find(key);
				sb.append(a.getSearchText());
			} else if (check.equals("U") || check.equals("G")) {
				User u = userDAO.find(key);
				sb.append(u.getSearchText());
			} else if (check.equals("E")) {
				Employee e = empDAO.find(key);
				sb.append(e.getSearchText());
			}
		}
		output = sb.toString() + "FULL|Click to do a full search|"
				+ searchTerm.replace(" ", "+");

	}

	public boolean checkCon(int id) {
		return ht.containsValue(id);
	}

	private void buildPages(int total, int start, int end, int page) {
		pageLinks = LinkBuilder.getPageNOfXLinks(total, PAGEBREAK, start, end,
				page);
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

	public SearchEngine getSearchEngine() {
		return searchEngine;
	}

	public void setSearchEngine(SearchEngine searchEngine) {
		this.searchEngine = searchEngine;
	}

}
