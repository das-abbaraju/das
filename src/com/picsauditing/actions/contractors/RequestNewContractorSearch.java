package com.picsauditing.actions.contractors;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.Strings;
import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

@SuppressWarnings("serial")
public class RequestNewContractorSearch extends PicsActionSupport {
	private static Logger logger = LoggerFactory.getLogger(RequestNewContractorSearch.class);
	private Database database = new Database();
	private String term;
	private String type;
	private List<String> unusedTerms = new ArrayList<String>();
	private List<String> usedTerms = new ArrayList<String>();
	private Set<SearchResult> results = new TreeSet<SearchResult>();

	public String search() throws Exception {
		SearchEngine searchEngine = new SearchEngine(permissions);
		List<BasicDynaBean> matches = newGap(searchEngine, term, type);

		if (matches == null || matches.isEmpty()) {
			output = "No matches";
			return BLANK;
		}

		List<BasicDynaBean> data = database.select(buildQuery(buildIDList(matches)), false);
		Hashtable<Integer, Integer> workingForOperator = searchEngine.getConIds(permissions);

		for (BasicDynaBean contractor : data) {
			try {
				String name = contractor.get("name").toString();
				int id = Integer.parseInt(contractor.get("id").toString());

				results.add(new SearchResult(id, name, workingForOperator.contains(id)));
			} catch (Exception e) {
				logger.error("Error reading contractor name or parsing ID", e);
			}
		}

		return SUCCESS;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getUnusedTerms() {
		return unusedTerms;
	}

	public List<String> getUsedTerms() {
		return usedTerms;
	}

	public Set<SearchResult> getResults() {
		return results;
	}

	private List<BasicDynaBean> newGap(SearchEngine searchEngine, String term, String type) {
		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		List<String> termsArray = searchEngine.sortSearchTerms(searchEngine.buildTerm(term, false, false), true);

		while (results.isEmpty() && termsArray.size() > 0) {
			String query = searchEngine.buildQuery(null, termsArray, (Strings.isEmpty(type) ? null : "i1.indexType = '"
					+ Strings.escapeQuotes(type) + "'"), null, 20, false, true);
			try {
				results = database.select(query, false);
			} catch (SQLException e) {
				logger.error("Error running query", e.getStackTrace());
				return null;
			}

			if (!searchEngine.getNullTerms().isEmpty() && unusedTerms.isEmpty()) {
				unusedTerms.addAll(searchEngine.getNullTerms());
				termsArray.removeAll(searchEngine.getNullTerms());
			}

			usedTerms = termsArray;
			termsArray = termsArray.subList(0, termsArray.size() - 1);
		}

		return results;
	}

	private String buildQuery(List<Integer> ids) {
		StringBuilder query = new StringBuilder();

		if ("C".equalsIgnoreCase(type)) {
			query.append("SELECT a.id, a.name FROM accounts a WHERE a.type = 'Contractor' AND a.id IN (");
		} else if ("U".equalsIgnoreCase(type)) {
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