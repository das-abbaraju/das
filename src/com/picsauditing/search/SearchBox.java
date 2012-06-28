package com.picsauditing.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ArrayListMultimap;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.EmployeeDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.AbstractIndexableTable;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Employee;
import com.picsauditing.jpa.entities.Indexable;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.LinkBuilder;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class SearchBox extends PicsActionSupport implements Preparable {

	@Autowired
	private AccountDAO accountDAO;
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;
	@Autowired
	private EmployeeDAO empDAO;
	@Autowired
	private UserDAO userDAO;

	protected String searchTerm;

	protected int searchID = 0;
	protected int totalRows;
	protected int startIndex;
	protected String searchType = "";
	protected String pageLinks;

	protected List<AbstractIndexableTable> fullList;
	protected Hashtable<Integer, Integer> ht;

	protected Database db = new Database();

	private final int PAGEBREAK = 50;
	private static final String ignoreTerms = "'united states','us','contractor','inc','user','operator', 'and'";

	@Override
	public void prepare() throws Exception {
		String[] urlQuery = (String[]) ActionContext.getContext().getParameters().get("q");
		if (urlQuery != null) {
			searchTerm = urlQuery[0];
		}
	}

	public String execute() throws Exception {
		SearchEngine searchEngine = new SearchEngine(permissions);

		String result = BLANK;

		if ("getResult".equals(button)) {
			// pull up a result
			result = buttonGetResult(searchEngine);
		} else if ("search".equals(button)) {
			// full view and paging
			result = buttonSearch(searchEngine);
		} else {
			// autosuggest/complete
			result = buttonAutocomplete(searchEngine);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public String json() throws Exception {
		execute();

		JSONArray outputAsJsonArray = convertOutputToJson(output);
		json.put("results", outputAsJsonArray);

		return JSON;
	}

	@SuppressWarnings("unchecked")
	private JSONArray convertOutputToJson(String output) {
		JSONArray jsonArray = new JSONArray();

		String[] lines = output.split("\n");
		for (String line : lines) {
			String[] fields = line.split("\\|");

			// Make sure it's well formed
			if (fields.length < 5)
				continue;

			JSONObject jsonResult = new JSONObject();

			jsonResult.put("type", fields[1]);
			jsonResult.put("id", fields[2]);
			jsonResult.put("name", fields[3]);
			jsonResult.put("at", fields[4]);

			jsonArray.add(jsonResult);
		}

		return jsonArray;
	}

	private String buttonAutocomplete(SearchEngine searchEngine) throws Exception {
		List<String> terms = searchEngine.buildTerm(searchTerm, true, true);

		if (terms == null || terms.isEmpty()) {
			output = "NULL|" + getTextParameterized("MainSearch.NoReturnedResults", searchTerm) + "|";
			return BLANK;
		} else if (terms.get(0).equalsIgnoreCase("Audit") && terms.size() == 2 && permissions.isPicsEmployee()) {
			int auditID = Integer.parseInt(terms.get(1));
			ContractorAudit audit = contractorAuditDAO.find(auditID);
			if (audit != null) {
				output = audit.getSearchText();
			}
		} else {
			String query = searchEngine.buildQuery(permissions, terms, "i1.indexType NOT IN ('T','G')", 0, 10,
					false, false);
			List<BasicDynaBean> queryList = db.select(query, true);
			totalRows = db.getAllRows();

			if (queryList != null && queryList.size() > 0) {
				getResults(queryList);
			} else {
				queryList = db.select(searchEngine.buildAccountSearch(permissions, terms), true);
				getResults(queryList);
			}
		}

		return BLANK;
	}

	private String buttonSearch(SearchEngine searchEngine) throws Exception {
		List<String> terms = searchEngine.buildTerm(searchTerm, true, true);

		if (terms == null || terms.isEmpty()) {
			addActionMessage(getText("MainSearch.NoSearchableTerms"));

			return SUCCESS;
		}

		if (terms.get(0).equalsIgnoreCase("Audit") && terms.size() == 2 && permissions.isPicsEmployee()) {
			int auditID = Integer.parseInt(terms.get(1));
			ContractorAudit audit = contractorAuditDAO.find(auditID);
			if (audit != null) {
				return setUrlForRedirect(audit.getViewLink());
			}

			return SUCCESS;
		}

		// if corporate then build list of contractors in their system
		ht = searchEngine.getConIds(permissions);
		String query = searchEngine.buildQuery(permissions, terms, "i1.indexType NOT IN ('T','G')", startIndex,
				50, false, true);
		List<BasicDynaBean> queryList = db.select(query, true);
		totalRows = db.getAllRows();

		if (totalRows > PAGEBREAK) {
			String commonTermQuery = searchEngine.buildCommonTermQuery(terms, ignoreTerms, totalRows);
			List<BasicDynaBean> commonList = db.select(commonTermQuery, false);
			searchEngine.buildCommonSuggest(commonList, searchTerm);
		}

		if (queryList != null && queryList.size() > 0) {
			fullList = getFullResults(queryList);
		} else {
			queryList = db.select(searchEngine.buildAccountSearch(permissions, terms), true);
			fullList = getFullResults(queryList);
		}

		if (fullList == null) {
			return SUCCESS;
		}

		if (!Strings.isEmpty(url)) {
			return REDIRECT;
		}

		int end = 0;
		if (totalRows - (startIndex + 1) < PAGEBREAK) {
			end = totalRows;
		} else {
			end = startIndex + PAGEBREAK;
		}

		buildPages(totalRows, startIndex + 1, end, startIndex / PAGEBREAK + 1);

		return SUCCESS;
	}

	private String buttonGetResult(SearchEngine searchEngine) throws IOException {
		Indexable record = null;

		if ("account".equals(searchType)) {
			record = accountDAO.find(searchID);
		} else if ("user".equals(searchType)) {
			record = userDAO.find(searchID);
		} else if ("employee".equals(searchType)) {
			record = empDAO.find(searchID);
		} else if ("audit".equals(searchType) && permissions.isPicsEmployee()) {
			record = contractorAuditDAO.find(searchID);
		}

		if (record != null) {
			return setUrlForRedirect(record.getViewLink());
		} else {
			addActionError(getText("MainSearch.ErrorOccuredTryAgain"));
		}

		return BLANK;
	}

	public boolean isLoggedIn(boolean anonymous) {
		if (!anonymous) {
			if (ServletActionContext.getRequest().getRequestURI().endsWith("Ajax.action")) {
				loadPermissions();

				if (!permissions.isLoggedIn()) {
					return false;
				}
			} else if (!forceLogin()) {
				return false;
			}
		}

		return true;
	}

	private List<AbstractIndexableTable> getFullResults(List<BasicDynaBean> queryList) throws IOException {
		List<AbstractIndexableTable> records = getRecords(queryList);
		if (records.size() == 1) {
			Indexable viewThis = records.get(0);
			String viewAction = viewThis.getViewLink();

			if (permissions.isOperatorCorporate()) {
				if (viewThis instanceof ContractorAccount) {
					/*
					 * if this is a contractor, we only want to redirect it to
					 * its viewAction if the operator can view it otherwise we
					 * want to send them to the SearchForNew page.
					 */
					if (checkCon(viewThis.getId())) {
						setUrlForRedirect(viewAction);
					}
				}
			} else {
				setUrlForRedirect(viewAction);
			}
		}

		return records;
	}

	private void getResults(List<BasicDynaBean> queryList) {
		StringBuilder sb = new StringBuilder();
		List<AbstractIndexableTable> records = getRecords(queryList);

		if (records.size() > 0) {
			for (Indexable value : records)
				sb.append(value.getSearchText());
		}

		output = sb.toString() + "FULL|" + getText("MainSearch.ClickFullSearch") + "|" + searchTerm.replace(" ", "+");
	}

	public List<AbstractIndexableTable> getRecords(List<BasicDynaBean> queryList) {
		ArrayListMultimap<Class<? extends AbstractIndexableTable>, Integer> indexableMap = ArrayListMultimap.create();
		SearchList recordsList = new SearchList();

		for (BasicDynaBean bdb : queryList) {
			String check = (String) bdb.get("indexType");
			int fkID = Integer.parseInt(bdb.get("foreignKey").toString());
			if (Strings.isEmpty(check)) {
				check = "A";
			}

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

		for (Class<? extends AbstractIndexableTable> key : indexableMap.keySet()) {
			List<? extends AbstractIndexableTable> list = accountDAO.findWhere(key,
					"t.id IN (" + Strings.implode(indexableMap.get(key)) + ")", 0);
			if (list != null) {
				for (AbstractIndexableTable indexEntry : list) {
					SearchItem searchRecord = new SearchItem(key, indexEntry.getId(), indexEntry);
					recordsList.add(searchRecord);
				}
			}
		}

		return recordsList.getRecordsOnly(false);
	}

	public boolean checkCon(int id) {
		return ht.containsValue(id);
	}

	private void buildPages(int total, int start, int end, int page) {
		pageLinks = LinkBuilder.getPageNOfXLinks(total, PAGEBREAK, start, end, page);
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
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

	public List<AbstractIndexableTable> getFullList() {
		return fullList;
	}

	public void setFullList(List<AbstractIndexableTable> fullList) {
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

	public int getPAGEBREAK() {
		return PAGEBREAK;
	}

	class SearchItem {
		public int id;
		public Class<? extends AbstractIndexableTable> type = null;
		public AbstractIndexableTable record = null;

		public SearchItem(Class<? extends AbstractIndexableTable> type, int id) {
			this.type = type;
			this.id = id;
		}

		public SearchItem(Class<? extends AbstractIndexableTable> type, int id, AbstractIndexableTable record) {
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
				if (this.id == si.id && this.type == si.type) {
					return true;
				}
			} catch (Exception e) {
				Logger logger = LoggerFactory.getLogger(SearchBox.class);
				logger.error("Error in equals for SearchItem");
			}

			return false;
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

			if (!found) {
				data.add(item);
			}

			return item;
		}

		public List<AbstractIndexableTable> getRecordsOnly(boolean nullsAllowed) {
			List<AbstractIndexableTable> recordsOnly = new ArrayList<AbstractIndexableTable>();
			for (SearchItem item : data) {
				if (nullsAllowed) {
					recordsOnly.add(item.record);
				} else {
					if (item.record != null) {
						recordsOnly.add(item.record);
					}
				}
			}

			return recordsOnly;
		}
	}
}
