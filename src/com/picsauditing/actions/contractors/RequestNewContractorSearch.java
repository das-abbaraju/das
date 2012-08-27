package com.picsauditing.actions.contractors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.search.Database;
import com.picsauditing.search.SearchEngine;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class RequestNewContractorSearch extends PicsActionSupport {
	private static Logger logger = LoggerFactory.getLogger(RequestNewContractorSearch.class);

	public static List<String> unusedTerms;
	public static List<String> usedTerms;

	private Database database = new Database();
	private String term;
	private String type;
	private boolean continueCheck = true;

	@SuppressWarnings("unchecked")
	public String ajaxCheck() throws Exception {
		SearchEngine searchEngine = new SearchEngine(permissions);
		List<BasicDynaBean> matches = newGap(searchEngine, term, type);

		if (matches != null && !matches.isEmpty()) {
			continueCheck = false;
		} else {
			return null;
		}

		JSONArray result = buildUsedAndUnusedTerms();

		List<BasicDynaBean> data = database.select(buildQuery(buildIDList(matches)), false);
		final Hashtable<Integer, Integer> ht = searchEngine.getConIds(permissions);

		for (BasicDynaBean contractor : data) {
			final String name = contractor.get("name").toString();
			final String id = contractor.get("id").toString();
			result.add(new JSONObject() {
				{
					put("name", name);
					put("id", id);
					if (ht.containsKey(id)) {
						put("add", false);
					} else {
						put("add", true);
					}
				}
			});
		}

		json.put("result", result);
		return JSON;
	}

	public static List<BasicDynaBean> newGap(SearchEngine searchEngine, String term, String type) {
		unusedTerms = new ArrayList<String>();
		usedTerms = new ArrayList<String>();

		List<BasicDynaBean> results = new ArrayList<BasicDynaBean>();
		Database db = new Database();
		List<String> termsArray = searchEngine.sortSearchTerms(searchEngine.buildTerm(term, false, false), true);

		while (results.isEmpty() && termsArray.size() > 0) {
			String query = searchEngine.buildQuery(null, termsArray, (Strings.isEmpty(type) ? null : "i1.indexType = '"
					+ Strings.escapeQuotes(type) + "'"), null, 20, false, true);
			try {
				results = db.select(query, false);
			} catch (SQLException e) {
				logger.error("Error running query in RequestNewCon");
				logger.error("{}", e.getStackTrace());
				return null;
			}
			if (!searchEngine.getNullTerms().isEmpty() && unusedTerms.isEmpty()) {
				unusedTerms.addAll(searchEngine.getNullTerms());
				termsArray.removeAll(searchEngine.getNullTerms());
			}
			usedTerms = termsArray;
			termsArray = termsArray.subList(0, termsArray.size() - 1);
			// termsArray.subList(1, termsArray.size());
		}

		return results;
	}

	@SuppressWarnings("unchecked")
	private JSONArray buildUsedAndUnusedTerms() {
		JSONArray result = new JSONArray();
		result.add(continueCheck);

		JSONArray jObj = new JSONArray();
		for (final String str : unusedTerms) {
			jObj.add(new JSONObject() {
				{
					put("unused", str);
				}
			});
		}
		result.add(jObj);

		jObj = new JSONArray();
		for (final String str : usedTerms) {
			jObj.add(new JSONObject() {
				{
					put("used", str);
				}
			});
		}

		return result;
	}

	private String buildQuery(List<Integer> ids) {
		StringBuilder query = new StringBuilder();

		if ("C".equalsIgnoreCase(type)) {
			query.append("SELECT a.id, a.name FROM accounts a WHERE a.id IN (");
		} else if ("U".equalsIgnoreCase(type)) {
			query.append("SELECT a.id, a.name FROM accounts a JOIN users u ON a.id = u.accountID WHERE a.id IN(");
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
}