package com.picsauditing.search;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;

import com.google.common.collect.ArrayListMultimap;
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
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class MainSearch extends PicsActionSupport implements Preparable {

	protected String searchTerm;
	protected boolean orderByName = false;

	protected int searchID = 0;
	protected int totalRows;
	protected int startIndex;
	protected String searchType = "";
	protected String accType = "";
	protected String pageLinks;

	private final int PAGEBREAK = 50;

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

	@Override
	public void prepare() throws Exception {
		String[] qA = (String[]) ActionContext.getContext().getParameters().get("q");
		if (qA != null)
			searchTerm = qA[0];
	}

	public String execute() throws SQLException, IOException {
		if (ServletActionContext.getRequest().getRequestURI().endsWith("Ajax.action")) {
			loadPermissions();
			if (!permissions.isLoggedIn())
				return BLANK;
		} else if (!forceLogin()) {
			return LOGIN;
		}
		searchEngine = new SearchEngine(permissions);
		if ("getResult".equals(button)) { // pull up a result
			Indexable record = null;
			if("account".equals(searchType))
				record = accountDAO.find(searchID);
			else if("user".equals(searchType))
				record = userDAO.find(searchID);
			else if("employee".equals(searchType))
				record = empDAO.find(searchID);
			if(record!=null)
				redirect(record.getViewLink());
			else
				addActionError("An Error occured with your search result, please try again.");

			return BLANK;
		} else if ("search".equals(button)) { // full view and paging

			List<String> terms = searchEngine.buildTerm(searchTerm, true, false);
			if(terms==null || terms.isEmpty()){
				addActionMessage("No searchable terms, please try again");
				return SUCCESS;
			}
			// if corporate then build list of contractors in their system
			ht = searchEngine.getConIds(permissions);
			String query = searchEngine.buildQuery(permissions, terms, null, startIndex, 50, false, true);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			String commonTermQuery = searchEngine.buildCommonTermQuery(terms, totalRows);

			if (totalRows > PAGEBREAK) {
				List<BasicDynaBean> commonList = db.select(commonTermQuery, false);
				searchEngine.buildCommonSuggest(commonList, searchTerm);
			}

			if (queryList != null && queryList.size() > 0)
				fullList = getFullResults(queryList);
			else {
				queryList = db.select(searchEngine.buildAccountSearch(permissions, terms), true);
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
			List<String> terms = searchEngine.buildTerm(searchTerm, true, false);
			if(terms==null || terms.isEmpty()){
				output = "NULL|-"+searchTerm+"- did not return any results. Please try different a different search|";
				return BLANK;
			}
			String query = searchEngine.buildQuery(permissions, terms, null, 0, 10, false, false);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();
			if (queryList != null && queryList.size() > 0)
				getResults(queryList);
			else {
				queryList = db.select(searchEngine.buildAccountSearch(permissions, terms), true);
				getResults(queryList);
			}

			return BLANK;
		}
	}

	private List<Indexable> getFullResults(List<BasicDynaBean> queryList) throws IOException {
		List<Indexable> records = getRecords(queryList, orderByName);
		if (records.size() == 1) {
			Indexable viewThis = records.get(0);
			String viewAction = viewThis.getViewLink();
			redirect(viewAction);
		}
		return records;
	}

	private void getResults(List<BasicDynaBean> queryList) {
		StringBuilder sb = new StringBuilder();
		List<Indexable> records = getRecords(queryList, false);
		if (records.size() > 0) {
			for (Indexable value : records)
				sb.append(value.getSearchText());
		}
		output = sb.toString() + "FULL|Click to do a full search|" + searchTerm.replace(" ", "+");
	}

	@SuppressWarnings("unchecked")
	public List<Indexable> getRecords(List<BasicDynaBean> queryList, boolean rOrderByName) {
		ArrayListMultimap<Class, Integer> indexableMap = ArrayListMultimap.create();
		SearchList recordsList = new SearchList();
		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int fkID = Integer.parseInt(bdb.get("foreignKey").toString());
			if (Strings.isEmpty(check))
				check = "A";
			if (check.equals("A") || check.equals("AS") || check.equals("C") || check.equals("CO") || check.equals("O")) {
				SearchItem searchRecord = new SearchItem(Account.class, fkID);
				indexableMap.put(Account.class, fkID);
				recordsList.add(searchRecord);
			} else if (check.equals("U") || check.equals("G")) {
				SearchItem searchRecord = new SearchItem(User.class, fkID);
				indexableMap.put(User.class, fkID);
				recordsList.add(searchRecord);
			} else if (check.equals("E")) {
				SearchItem searchRecord = new SearchItem(Employee.class, fkID);
				indexableMap.put(Employee.class, fkID);
				recordsList.add(searchRecord);
			}
		}
		for (Class key : indexableMap.keySet()) {
			List<Indexable> list = accountDAO.findWhere(key.getName(), "t.id IN ("
					+ Strings.implode(indexableMap.get(key)) + ")", 0);
			if (list != null) {
				for (Indexable indexEntry : list) {
					SearchItem searchRecord = new SearchItem(key, indexEntry.getId(), indexEntry);
					recordsList.add(searchRecord);
				}
			}
		}
		return recordsList.getRecordsOnly(false, rOrderByName);
	}

	public boolean checkCon(int id) {
		return ht.containsValue(id);
	}

	private void buildPages(int total, int start, int end, int page) {
		pageLinks = LinkBuilder.getPageNOfXLinks(total, PAGEBREAK, start, end, page);
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

	public boolean isOrderByName() {
		return orderByName;
	}

	public void setOrderByName(boolean orderByName) {
		this.orderByName = orderByName;
	}

}

class SearchItem {
	public int id;
	public Class<? extends Indexable> type = null;
	public Indexable record = null;

	public SearchItem(Class<? extends Indexable> type, int id) {
		this.type = type;
		this.id = id;
	}

	public SearchItem(Class<? extends Indexable> type, int id, Indexable record) {
		this.type = type;
		this.id = id;
		this.record = record;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		try {
			SearchItem si = (SearchItem) o;
			if (this.id == si.id && this.type == si.type)
				return true;
			else
				return false;
		} catch (Exception e) {
			System.out.println("Error in equals for SearchItem");
			return false;
		}
	}

	@Override
	public int hashCode() {
		return ((type.getName().hashCode() % 1000) * 10000000) + id;
	}

}

class SearchList {
	public List<SearchItem> data = null;

	public SearchList() {
		data = new ArrayList<SearchItem>();
	}

	public SearchItem add(SearchItem item) {
		boolean found = false;
		for (SearchItem other : data) {
			if (other.equals(item)) {
				other.record = item.record;
				found = true;
				break;
			}
		}
		if (!found)
			data.add(item);
		return item;
	}

	public List<Indexable> getRecordsOnly(boolean nullsAllowed, boolean sortByName) {
		List<Indexable> recordsOnly = new ArrayList<Indexable>();
		for (SearchItem item : data) {
			if (nullsAllowed)
				recordsOnly.add(item.record);
			else {
				if (item.record != null)
					recordsOnly.add(item.record);
			}
		}
		if(sortByName){
			Collections.sort(recordsOnly, new Comparator<Indexable>() {
				@Override
				public int compare(Indexable o1, Indexable o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
		}
		return recordsOnly;
	}
}
