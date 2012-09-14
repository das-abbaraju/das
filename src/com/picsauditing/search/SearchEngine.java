package com.picsauditing.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

public class SearchEngine {
	private static final Logger logger = LoggerFactory.getLogger(SearchEngine.class);
	private static final Marker marker = MarkerFactory.getMarker("Search Engine");

	protected String searchTerm;

	protected String orderBy = null;

	protected final String indexTable = "app_index";
	protected final String indexStats = "app_index_stats";

	protected List<String> commonFilterSuggest = new ArrayList<String>();
	protected List<String> nullTerms = new ArrayList<String>();

	protected Database db = new Database();
	protected Permissions perm;

	public SearchEngine(Permissions perm) {
		this.perm = perm;
	}

	/**
	 * Gets ids of contractors in a users system
	 * 
	 * @param perm
	 *            Permission of user doing search
	 * @return Hashtable of ids of contractors in the users system
	 */
	public Hashtable<Integer, Integer> getConIds(Permissions perm) {
		Hashtable<Integer, Integer> results = new Hashtable<Integer, Integer>();
		String conQuery = "";
		if (perm.isCorporate()) {
			conQuery = "SELECT gc.subID id FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID ="
					+ perm.getAccountId() + " GROUP BY id";
		} else if (perm.isOperator()) {
			conQuery = "SELECT gc.subID id FROM generalcontractors gc WHERE gc.genID = " + perm.getAccountId();
		} else
			return results;
		List<BasicDynaBean> temp;
		try {
			temp = db.select(conQuery, false);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		results = new Hashtable<Integer, Integer>(temp.size());
		for (BasicDynaBean bdb : temp) {
			int value = Integer.parseInt(bdb.get("id").toString());
			results.put(value, value);
		}
		return results;
	}

	/**
	 * Builds a query for finding common terms in a search term
	 * 
	 * @param terms
	 *            The terms used in the search
	 * @param ignore
	 *            (Optional) List of terms to explicitly ignore
	 * @param total
	 *            count of the term is divided by this to see if it's included
	 *            in this query
	 * @return A String containing the search query
	 */
	public String buildCommonTermQuery(List<String> terms, String ignore, int total) {
		String sub = buildQuery(perm, terms, null, null, total, true, true);
		StringBuilder cSb = new StringBuilder();
		cSb.append("SELECT a.value term, COUNT(a.value) cc FROM ").append(indexTable).append(" a JOIN (");
		cSb.append(sub)
				.append(") AS r1 ON a.foreignKey = r1.foreignKey\nWHERE a.value NOT IN (SELECT isoCode FROM ref_country_subdivision)");
		if (ignore != null && ignore.length() > 0)
			cSb.append(" AND a.value NOT IN (").append(ignore).append(")");
		cSb.append(" GROUP BY a.value HAVING cc/").append(total).append(" <.8\n").append("ORDER BY cc DESC LIMIT 10");
		return cSb.toString();
	}

	public void buildCommonSuggest(List<BasicDynaBean> commonList, String check) {
		List<String> sA = buildTerm(check, true, false);
		for (BasicDynaBean bdb : commonList) {
			String term = bdb.get("term").toString();
			for (String str : sA) {
				if (term.equals(str))
					break;
			}
			commonFilterSuggest.add(term);
		}
	}

	/**
	 * Builds the Query based on term and returns it as a string
	 * 
	 * @param currPerm
	 *            permissions to use, pass in null to do an unrestricted search
	 * @param terms
	 *            Term to use to search for
	 * @param extraWhere
	 *            Additional where to limit query
	 * @param start
	 *            Row to start at
	 * @param limit
	 *            Limit for Search
	 * @param buildCommon
	 *            If True then skip over total rows and various other parts of
	 *            query
	 * @param fullSearch
	 *            True for full search, false for 10 result ajax search
	 * @return A string that is the query to run using db.select
	 */
	public String buildQuery(Permissions currPerm, List<String> terms, String extraWhere, Integer start, Integer limit,
			boolean buildCommon, boolean fullSearch) {
		StringBuilder sql = new StringBuilder();

		sql.append("SELECT ");
		if (!buildCommon)
			sql.append("SQL_CALC_FOUND_ROWS ");

		if (terms == null)
			return null;

		if (currPerm != null && currPerm.isOperatorCorporate())
			sql.append("rName,");
		sql.append("foreignKey");
		if (!buildCommon) {
			sql.append(",indexType, MIN(ttotal*(v1total/i1weight");
			// TODO change from / to *
			for (int i = 1; i < terms.size(); i++) {
				sql.append("+");
				sql.append("v").append(i + 1).append("total/i").append(i + 1).append("weight");
			}
			sql.append(")) score, (i1value = '").append(terms.get(0)).append("') * i1weight m");
		}

		sql.append("\n FROM ( ");

		// build the inner search query (for the first term)
		StringBuilder innerSql = new StringBuilder();

		innerSql.append(
				" SELECT i1.foreignKey AS foreignKey, i1.indexType AS indexType, i1.value AS i1value, i1.weight AS i1weight, t.total AS ttotal, v1.total AS v1total ")
				.append(" FROM ").append(" ").append(indexTable).append(" i1 ").append(" LEFT JOIN ")
				.append(indexStats).append(" t ON i1.indexType = t.indexType AND t.value IS NULL \n")
				.append(" LEFT JOIN ").append(indexStats)
				.append(" v1 ON v1.indexType IS NULL AND i1.value = v1.value ").append(" WHERE 1 ")
				.append(" AND (i1.value LIKE '" + terms.get(0) + "%') ");

		if (!Strings.isEmpty(extraWhere))
			innerSql.append("\n AND (").append(extraWhere).append(") ");

		// build inner wrapper for header and footer
		StringBuilder wrapperHeader = new StringBuilder();
		StringBuilder wrapperFooter = new StringBuilder();

		for (int i = 1; i < terms.size(); i++) {
			String tTerm = "t" + i;
			String iTerm = "i" + (i + 1);
			String vTerm = "v" + (i + 1);

			if (i != terms.size()) {
				String startString = wrapperHeader.toString();

				wrapperHeader = new StringBuilder().append("select ").append(tTerm).append(".*, ").append(iTerm)
						.append(".weight AS ").append(iTerm).append("weight, ").append(vTerm).append(".total AS ")
						.append(vTerm).append("total ");
				wrapperHeader.append(" FROM ( \n").append(startString);
			}

			wrapperFooter.append(") ").append(tTerm);
			wrapperFooter.append("\n JOIN ").append(indexTable).append(" ").append(iTerm).append(" ON ").append(tTerm)
					.append(".indexType = ").append(iTerm).append(".indexType ").append(" AND ").append(tTerm)
					.append(".foreignKey = ").append(iTerm).append(".foreignKey ").append(" AND ").append(iTerm)
					.append(".value LIKE '").append(terms.get(i)).append("%'\n");
			wrapperFooter.append("\n JOIN ").append(indexStats).append(" ").append(vTerm).append(" ON ").append(vTerm)
					.append(".indexType IS NULL ").append(" AND ").append(iTerm).append(".value = ").append(vTerm)
					.append(".value ");
		}

		// concat all the values so far
		sql.append(wrapperHeader.toString()).append(innerSql.toString()).append(wrapperFooter.toString());

		sql.append("\n ) t").append(terms.size());

		if (currPerm != null) {
			String accountStatuses = "'Requested','Active','Pending'";
			String userStatuses = "'yes'";
			if (currPerm.isPicsEmployee() || currPerm.getAccountStatus().isDemo())
				accountStatuses += ",'Demo'";

			if (currPerm.isCorporate()) {
				sql.append("\nJOIN ((\nSELECT a.name rName, a.id id, acc.rType FROM accounts a JOIN\n")
						.append("((SELECT f.opID id, 'O' rType FROM facilities f WHERE f.corporateID =")
						.append(currPerm.getAccountId()).append(')');
				sql.append("\nUNION\n")
						.append("(SELECT a.id, IF(a.type = 'Corporate', 'CO', 'O') rType FROM accounts a JOIN operators o USING(id) WHERE o.parentID =")
						.append(currPerm.getAccountId())
						.append(")) AS acc on a.id = acc.id AND a.status IN (" + accountStatuses + ")\n)\n");
				if (fullSearch) {
					sql.append("UNION\n(SELECT name rName, id, 'C' rType FROM accounts WHERE type = 'Contractor' AND status IN ("
							+ accountStatuses + "))\n");
				} else {
					sql.append("UNION\n(SELECT a.name rName, a.id, acc.rType FROM accounts a JOIN\n")
							.append("(SELECT gc.subID id, 'C' rType FROM generalcontractors gc\nJOIN facilities f ON f.opID = gc.genID AND f.corporateID =")
							.append(currPerm.getAccountId())
							.append(" GROUP BY id) AS acc on a.id = acc.id WHERE a.status IN (" + accountStatuses
									+ "))\n");
				}
				if (currPerm.hasPermission(OpPerms.EditUsers)) {
					sql.append(
							"UNION\n(SELECT u.name rName, u.id, IF(u.isGroup='Yes','G','U') rType FROM users u JOIN\n"
									+ "((select f.opID id FROM facilities f WHERE f.corporateID =")
							.append(currPerm.getAccountId())
							.append(")\nUNION\n(SELECT o.id id FROM operators o WHERE o.parentID =")
							.append(currPerm.getAccountId()).append(")\n) AS t ON u.accountID = t.id")
							.append(" where u.isActive = " + userStatuses + ")");
				}
				if (currPerm.hasPermission(OpPerms.ManageEmployees)) {
					sql.append(
							"\nUNION\n(\nSELECT CONCAT(e.firstName, ' ', e.lastName) rName, e.id, 'E' rType FROM employee e join\n"
									+ "((SELECT f.opID id FROM facilities f WHERE f.corporateID =")
							.append(currPerm.getAccountId())
							.append(")\nUNION\n(SELECT o.id id from operators o where o.parentID =")
							.append(currPerm.getAccountId())
							.append(")\n")
							.append("UNION\n(select gc.subID FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID =")
							.append(currPerm.getAccountId()).append(")\n) AS rE on e.accountID = rE.id)\n");
				}
				sql.append(") AS r1\nON foreignKey = r1.id AND indexType = r1.rType");
			} else if (currPerm.isOperator()) {
				sql.append("\nJOIN ((\nSELECT a.name rName, a.id, acc.rType FROM accounts a JOIN \n")
						.append("(SELECT gc.subID id, 'C' rType FROM generalcontractors gc WHERE gc.genID =")
						.append(currPerm.getAccountId())
						.append(") AS acc ON a.id = acc.id WHERE a.status IN (" + accountStatuses + ") )");
				if (currPerm.hasPermission(OpPerms.EditUsers)) {
					sql.append(
							"\nUNION\n(SELECT u.name rName, u.id id, if(u.isGroup='Yes','G','U') rType FROM users u WHERE u.isActive = "
									+ userStatuses + " and u.accountID =").append(currPerm.getAccountId()).append(')');
				}
				if (currPerm.hasPermission(OpPerms.ManageEmployees)) {
					sql.append(
							"\nUNION\n(SELECT CONCAT(e.firstName, ' ', e.lastName) rName, e.id, 'E' rType FROM employee e JOIN "
									+ "generalcontractors gc ON gc.subID = e.accountID WHERE gc.genID =")
							.append(currPerm.getAccountId()).append(")");
				}
				sql.append("\n) AS r1\nON foreignKey = r1.id AND indexType = r1.rType");
			}
		}

		sql.append("\nGROUP BY foreignKey, indexType");
		if (buildCommon) {
			sql.append("\nORDER BY foreignKey");
		} else {
			sql.append("\nORDER BY m DESC, score, foreignKey");
		}
		if (limit != null) {
			sql.append("\nLIMIT ");

			if (start != null) {
				sql.append(start);
				sql.append(", ");
			}
			sql.append(limit);
		}

		logger.debug(marker, sql.toString());

		return sql.toString();
	}

	public String buildAccountSearch(Permissions currPerm, List<String> terms) {
		StringBuilder sb = new StringBuilder();
		SelectSQL sql = new SelectSQL("accounts a");
		PermissionQueryBuilder pb;
		sql.addField("a.id foreignKey, a.name, case a.type when 'Contractor'"
				+ " then 'C' when 'Corporate' then 'CO' when 'Operator' then 'O'"
				+ "when 'Assessment' then 'AS' end indexType");
		if (currPerm != null && !currPerm.isAdmin()) {
			sql.addWhere("a.status IN ('Requested','Active','Pending')");
		}
		sql.addOrderBy("a.name");
		for (String searchTerm : terms) {
			sb.append("(a.name LIKE '").append(searchTerm).append("%' OR a.nameIndex LIKE '").append(searchTerm)
					.append("%' OR a.id = '").append(searchTerm).append("')").append(" OR ");
		}
		sb.setLength(sb.lastIndexOf(" OR "));
		if (currPerm != null) {
			pb = new PermissionQueryBuilder(currPerm);
			pb.setAccountAlias("a");
			// ORs being performed along with ANDs between sb sql and pb sql
			// on later concatenate for multiple terms and not being explicit
			// with grouping
			if (sb.length() > 0 && pb.toString().length() > 0) {
				sb.insert(0, "(");
				sb.append(")");
			}
			sql.addWhere(sb.toString() + pb.toString());
		} else
			sql.addWhere(sb.toString());

		return sql.toString();
	}

	public String buildNativeOperatorSearch(Permissions currPerm, List<String> terms) {
		StringBuilder sb = new StringBuilder();
		SelectSQL sql = new SelectSQL("accounts a");
		sql.addField("o.*, a.*");
		if (currPerm != null && !currPerm.isAdmin()) {
			sql.addWhere("a.status IN ('Active','Pending')");
		}

		sql.addJoin("JOIN operators o ON a.id = o.id");
		sql.addJoin("LEFT JOIN ref_country rc ON rc.isoCode=a.country");
		sql.addJoin("LEFT JOIN ref_country_subdivision rs ON rs.isoCode=a.countrySubdivision");

		sql.addOrderBy("a.name");
		for (String searchTerm : terms) {
			sb.append("(a.name LIKE '").append(searchTerm).append("%' OR a.nameIndex LIKE '").append(searchTerm)
					.append("%' OR a.id = '").append(searchTerm).append("'");
			sb.append(" OR a.country LIKE '").append(searchTerm).append("%'");
			sb.append(" OR a.city LIKE '").append(searchTerm).append("%'");
			sb.append(" OR rc.english LIKE '").append(searchTerm).append("%'");
			sb.append(" OR rc.spanish LIKE '").append(searchTerm).append("%'");
			sb.append(" OR rc.french LIKE '").append(searchTerm).append("%'");
			sb.append(" OR a.countrySubdivision LIKE '").append(searchTerm).append("%'");
			sb.append(" OR rs.english LIKE '").append(searchTerm).append("%'");
			sb.append(" OR a.zip LIKE '").append(searchTerm).append("%'");
			sb.append(")").append(" OR ");
		}
		sb.setLength(sb.lastIndexOf(" OR "));
		sql.addWhere(sb.toString());
		sql.addWhere("a.type IN ('Operator')");

		if (currPerm != null && (currPerm.isMarketing() || currPerm.getAccountStatus().isDemo())) {
			sql.addWhere("a.status IN ('Active','Pending', 'Requested','Demo')");
		} else {
			sql.addWhere("a.status IN ('Active','Requested')");
		}

		return sql.toString();
	}

	/**
	 * Builds string array of terms from a string containing the search term
	 * 
	 * @param check
	 *            The String to use to build the terms
	 * @param sort
	 *            True if you want to sort based on commonality
	 * @param removeDups
	 *            True if you want to remove duplicates
	 * @return An array of search terms, sorted from least to most common
	 */
	public List<String> buildTerm(String check, boolean sort, boolean removeDups) {
		if (Strings.isEmpty(check))
			return Collections.emptyList();
		String[] terms = check.toUpperCase().split("\\W|_");
		Collection<String> stringCollection;
		if (removeDups)
			stringCollection = new HashSet<String>(terms.length);
		else
			stringCollection = new ArrayList<String>();
		for (int i = 0; i < terms.length; i++) {
			stringCollection.add(terms[i].replaceAll("^(HTTP)|^(W{3})", "").replaceAll("[^a-zA-Z0-9\\s]", ""));
		}
		// remove empty strings
		while (stringCollection.remove(""))
			;
		List<String> s = new ArrayList<String>();
		s.addAll(stringCollection);
		if (sort)
			return sortSearchTerms(s, false);
		else
			return s;
	}

	/**
	 * Sorts the terms based on commonality
	 * 
	 * @param terms
	 *            List of terms to sort
	 * @param onlyValid
	 *            If true then will remove terms that are not in the index
	 * @return
	 */
	public List<String> sortSearchTerms(List<String> terms, boolean onlyValid) {
		if (terms.size() <= 1)
			return terms;
		else if (terms.get(0).equalsIgnoreCase("Audit") && terms.size() == 2 && perm.isPicsEmployee())
			return terms;
		List<String> array;
		List<BasicDynaBean> l = null;
		String commonSql = "' term, SUM(total) t FROM " + indexStats + " WHERE value LIKE '";
		String commonEnd = " AND indexType IS NULL)";
		StringBuilder fullQuery = new StringBuilder();
		if (onlyValid)
			fullQuery.append("SELECT termsTable.term, termsTable.t FROM(");
		fullQuery.append("(SELECT '").append(0).append(commonSql).append(terms.get(0)).append("%'").append(commonEnd);
		for (int i = 1; i < terms.size(); i++) {
			fullQuery.append("\nUnion\n");
			fullQuery.append("(SELECT '").append(i).append(commonSql).append(terms.get(i)).append("%'")
					.append(commonEnd);
		}
		fullQuery.append(" ORDER BY t");
		if (onlyValid)
			fullQuery.append(") AS termsTable\n WHERE termsTable.t IS NOT NULL");
		try {
			l = db.select(fullQuery.toString(), false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		array = new ArrayList<String>(l.size());
		for (int i = 0; i < l.size(); i++) {
			int index = Integer.parseInt(l.get(i).get("term").toString());
			array.add(terms.get(index));
		}
		if (onlyValid) {
			if (terms.removeAll(array))
				nullTerms.addAll(terms);
		}
		return array;

	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List<String> getCommonFilterSuggest() {
		return commonFilterSuggest;
	}

	public void setCommonFilterSuggest(List<String> commonFilterSuggest) {
		this.commonFilterSuggest = commonFilterSuggest;
	}

	public List<String> getNullTerms() {
		return nullTerms;
	}

	public void setNullTerms(List<String> nullTerms) {
		this.nullTerms = nullTerms;
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public Permissions getPerm() {
		return perm;
	}

	public void setPerm(Permissions perm) {
		this.perm = perm;
	}

	public String getIndexTable() {
		return indexTable;
	}

	public String getIndexStats() {
		return indexStats;
	}

}
