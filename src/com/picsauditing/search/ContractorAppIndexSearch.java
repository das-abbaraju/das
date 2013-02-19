package com.picsauditing.search;

import com.picsauditing.access.Permissions;
import com.picsauditing.util.Strings;
import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

public class ContractorAppIndexSearch {
	public static final String INDEX_TYPE_CONTRACTOR = "C";
	public static final String INDEX_TYPE_USER = "U";

	private Logger logger = LoggerFactory.getLogger(ContractorAppIndexSearch.class);
	private Database database = new Database();
	private Permissions permissions;
	private SearchEngine searchEngine;
	private List<String> termsWithResults = new ArrayList<String>();

	private ContractorAppIndexSearch() {
	}

	public ContractorAppIndexSearch(Permissions permissions) {
		this.permissions = permissions;

		searchEngine = new SearchEngine(permissions);
	}

	public Set<SearchResult> searchOn(String searchString, String searchType) throws SQLException {
		Set<SearchResult> matches = new TreeSet<SearchResult>();
		List<BasicDynaBean> results = determineResultsFromBestSearchTerms(searchString, searchType);

		if (results != null && !results.isEmpty()) {
			buildContractorMatches(searchType, matches, results);
		}

		return matches;
	}

	public List<String> getTermsWithResults() {
		return termsWithResults;
	}

	/**
	 * Try running a search on all of the search terms. If nothing is found, remove null terms and try again with the last term removed.
	 *
	 * @param searchString
	 * @param searchType
	 * @return
	 */
	private List<BasicDynaBean> determineResultsFromBestSearchTerms(String searchString, String searchType) {
		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		List<String> termsArray = searchEngine.sortSearchTerms(searchEngine.buildTerm(searchString, false, false), true);

		while (results.isEmpty() && termsArray.size() > 0) {
			String query = searchEngine.buildQuery(null, termsArray, (Strings.isEmpty(searchType) ? null : "i1.indexType = '"
					+ Strings.escapeQuotes(searchType) + "'"), null, 20, false, true);

			try {
				results = database.select(query, false);
			} catch (SQLException e) {
				logger.error("Error running query", e.getStackTrace());
				return null;
			}

			if (!searchEngine.getNullTerms().isEmpty()) {
				termsArray.removeAll(searchEngine.getNullTerms());
				termsWithResults.clear();
				termsWithResults.addAll(termsArray);
			}

			termsArray = termsArray.subList(0, termsArray.size() - 1);
		}

		return results;
	}

	private String buildQuery(List<Integer> ids, String searchType) {
		StringBuilder query = new StringBuilder();

		if ("C".equalsIgnoreCase(searchType)) {
			query.append("SELECT a.id, a.name FROM accounts a WHERE a.type = 'Contractor' AND a.id IN (");
		} else if ("U".equalsIgnoreCase(searchType)) {
			query.append("SELECT a.id, a.name FROM accounts a JOIN users u ON a.id = u.accountID "
					+ "WHERE a.type = 'Contractor' AND a.id IN(");
		}

		query.append(Strings.implode(ids, ",")).append(')');

		return query.toString();
	}

	private List<Integer> buildIDList(List<BasicDynaBean> matches) {
		List<Integer> ids = new ArrayList<Integer>();

		for (BasicDynaBean match : matches) {
			int id = Integer.parseInt(match.get("foreignKey").toString());
			ids.add(id);
		}

		return ids;
	}

	private void buildContractorMatches(String searchType, Set<SearchResult> matches, List<BasicDynaBean> results) throws SQLException {
		List<BasicDynaBean> data = database.select(buildQuery(buildIDList(results), searchType), false);
		Hashtable<Integer, Integer> workingForOperator = searchEngine.getConIds(permissions);

		for (BasicDynaBean contractor : data) {
			try {
				String name = contractor.get("name").toString();
				int id = Integer.parseInt(contractor.get("id").toString());

				matches.add(new SearchResult(id, name, workingForOperator.contains(id)));
			} catch (Exception e) {
				logger.error("Error reading contractor name or parsing ID", e);
			}
		}
	}

	public class SearchResult implements Comparable<SearchResult> {
		private int id;
		private String name;
		private boolean worksForOperator;

		public SearchResult(int id, String name, boolean worksForOperator) {
			this.id = id;
			this.name = name;
			this.worksForOperator = worksForOperator;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isWorksForOperator() {
			return worksForOperator;
		}

		public void setWorksForOperator(boolean worksForOperator) {
			this.worksForOperator = worksForOperator;
		}

		@Override
		public int compareTo(SearchResult o) {
			if (this.name.equals(o.name)) {
				return this.id - o.id;
			}

			return this.name.compareTo(o.name);
		}
	}
}
