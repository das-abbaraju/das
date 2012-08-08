package com.picsauditing.search;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.collections.CollectionUtils;
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

/**
 * This is a controller. It should not use any DAOs from its parent.
 */
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

	private static final Logger logger = LoggerFactory.getLogger(SearchBox.class);

	private static final Set<String> accountIndexTypes = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList("A", "AS", "C", "CO", "O")));
	private static final Set<String> userIndexTypes = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList("U", "G")));
	private static final Set<String> employeeIndexTypes = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList("E")));
	private static final Set<String> nonsearchableIndexTypes = Collections.unmodifiableSet(
			new HashSet<String>(Arrays.asList("T", "AU")));

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
	public String json() {
		try {
			execute();
		} catch (Exception e) {
			logger.error("Exception while running execute.", e);
		}

		JSONArray outputAsJsonArray = convertOutputToJson(output);
		json.put("results", outputAsJsonArray);

		return JSON;
	}

	@SuppressWarnings("unchecked")
	public String userJson() {
		try {
			execute();
		} catch (Exception e) {
			logger.error("Exception while running execute.", e);
		}

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

			jsonResult.put("search_type", fields[0]);
			jsonResult.put("result_type", fields[1]);
			jsonResult.put("result_id", fields[2]);
			jsonResult.put("result_name", fields[3]);
			jsonResult.put("result_at", fields[4]);

			jsonArray.add(jsonResult);
		}

		return jsonArray;
	}

	private String buttonAutocomplete(SearchEngine searchEngine) throws Exception {
		List<String> terms = searchEngine.buildTerm(searchTerm, true, true);

		if (CollectionUtils.isEmpty(terms)) {
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

			if (CollectionUtils.isEmpty(queryList)) {
				queryList = db.select(searchEngine.buildAccountSearch(permissions, terms), true);
			}

			output = concatQueryResults(queryList);
		}

		return BLANK;
	}

	private String buttonSearch(SearchEngine searchEngine) throws Exception {
		List<String> terms = searchEngine.buildTerm(searchTerm, true, true);

		if (CollectionUtils.isEmpty(terms)) {
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

	private String concatQueryResults(List<BasicDynaBean> queryList) {
		StringBuilder builder = new StringBuilder();
		List<AbstractIndexableTable> records = getRecords(queryList);

		if (records.size() > 0) {
			for (Indexable value : records)
				builder.append(value.getSearchText());
		}

		return builder.toString();
	}

	private List<AbstractIndexableTable> getRecords(List<BasicDynaBean> queryList) {
		ArrayListMultimap<Class<? extends AbstractIndexableTable>, Integer> indexableMap = ArrayListMultimap.create();
		SearchList recordsList = new SearchList();

		for (BasicDynaBean queryResult : queryList) {
			String typeAbbrev = (String) queryResult.get("indexType");
			int foreignKeyId = Integer.parseInt(queryResult.get("foreignKey").toString());
			if (Strings.isEmpty(typeAbbrev)) {
				typeAbbrev = "A";
			}

			Class<? extends AbstractIndexableTable> recordClass = null;

			if (accountIndexTypes.contains(typeAbbrev)) {
				recordClass = Account.class;
			} else if (userIndexTypes.contains(typeAbbrev)) {
				recordClass = User.class;
			} else if (employeeIndexTypes.contains(typeAbbrev)) {
				recordClass = Employee.class;
			} else if (nonsearchableIndexTypes.contains(typeAbbrev)) {
				// Don't search for these types
			} else {
				logger.error("Unrecognized type abbreviation.");
			}

			if (recordClass == null)
				continue;

			SearchItem searchRecord = new SearchItem(recordClass, foreignKeyId);
			indexableMap.put(recordClass, foreignKeyId);
			recordsList.add(searchRecord);
		}

		for (Class<? extends AbstractIndexableTable> key : indexableMap.keySet()) {
			String query = "t.id IN (" + Strings.implode(indexableMap.get(key)) + ")";
			List<? extends AbstractIndexableTable> list = accountDAO.findWhere(key, query, 0);

			if (list == null)
				continue;

			for (AbstractIndexableTable indexEntry : list) {
				SearchItem searchRecord = new SearchItem(key, indexEntry.getId(), indexEntry);
				recordsList.add(searchRecord);
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
}
