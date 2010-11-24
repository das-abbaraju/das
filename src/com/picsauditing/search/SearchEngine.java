package com.picsauditing.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

public class SearchEngine {

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
			conQuery = "SELECT gc.subID id FROM generalcontractors gc WHERE gc.genID = "
					+ perm.getAccountId();
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
	 * @param total
	 *            count of the term is divided by this to see if it's included
	 *            in this query
	 * @return A String containing the search query
	 */
	public String buildCommonTermQuery(List<String> terms, int total) {
		String sub = buildQuery(perm, terms, null, null, total, true, true);
		StringBuilder cSb = new StringBuilder();
		cSb.append("SELECT a.value term, COUNT(a.value) cc FROM ").append(
				indexTable).append(" a JOIN (");
		cSb
				.append(sub)
				.append(
						") AS r1 ON a.foreignKey = r1.foreignKey\nWHERE a.value NOT IN "
								+ "(SELECT isoCode FROM ref_state)GROUP BY a.value HAVING cc/")
				.append(total).append(" <.8\n").append(
						"ORDER BY cc DESC LIMIT 10");
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
	public String buildQuery(Permissions currPerm, List<String> terms,
			String extraWhere, Integer start, Integer limit,
			boolean buildCommon, boolean fullSearch) {
		SelectSQL sql = new SelectSQL(indexTable + " i1");
		if (!buildCommon)
			sql.setSQL_CALC_FOUND_ROWS(true);
		if (terms == null)
			return null;
		StringBuilder sb = new StringBuilder();
		if (currPerm != null && currPerm.isOperatorCorporate())
			sb.append("rName,");
		sb.append("i1.foreignKey");
		if (!buildCommon) {
			sb.append(",i1.indexType, min(t.total*(v1.total/i1.weight"); 
			// TODO change from / to *
			for (int i = 1; i < terms.size(); i++) {
				sb.append("+");
				sb.append("v").append(i + 1).append(".total/i").append(i + 1)
						.append(".weight");
			}
			sb.append(")) score, (i1.value = '").append(terms.get(0)).append(
					"') * i1.weight m");
		}
		sql.addField(sb.toString());
		sb.setLength(0);
		sb.append("JOIN ").append(indexStats).append(
				" t ON i1.indexType = t.indexType AND t.value IS NULL\n");
		sb.append("JOIN ").append(indexStats).append(
				" v1 ON v1.indexType IS NULL and i1.value = v1.value");
		sql.addJoin(sb.toString());
		sb.setLength(0);
		for (int i = 1; i < terms.size(); i++) {
			sb.setLength(0);
			String vTerm = "v" + (i + 1);
			String iTerm = "i" + (i + 1);
			sb.append("JOIN ").append(indexTable).append(" ").append(iTerm)
					.append(" ON i1.indexType = ").append(iTerm).append(
							".indexType");
			sb.append(" AND i1.foreignKey = ").append(iTerm).append(
					".foreignKey AND ").append(iTerm).append(".value LIKE '")
					.append(terms.get(i)).append("%'\n");
			sb.append("JOIN ").append(indexStats).append(" ").append(vTerm)
					.append(" ON ").append(vTerm).append(".indexType IS NULL ");
			sb.append(" AND ").append(iTerm).append(".value = ").append(vTerm)
					.append(".value");
			sql.addJoin(sb.toString());
		}
		sb.setLength(0);
		if (currPerm != null) {
			if (currPerm.isCorporate()) {
				sb
						.append(
								"\nJOIN ((\nSELECT a.name rName, a.id id, acc.rType FROM accounts a JOIN\n")
						.append(
								"((SELECT f.opID id, 'O' rType FROM facilities f WHERE f.corporateID =")
						.append(currPerm.getAccountId()).append(')');
				sb
						.append("\nUNION\n")
						.append(
								"(SELECT a.id, IF(a.type = 'Corporate', 'CO', 'O') rType FROM accounts a JOIN operators o USING(id) WHERE o.parentID =")
						.append(currPerm.getAccountId()).append(
								")) AS acc on a.id = acc.id\n)\n");
				if (fullSearch) {
					sb
							.append("UNION\n(SELECT name rName, id, 'C' rType FROM accounts WHERE type = 'Contractor')\n");
				} else {
					sb
							.append(
									"UNION\n(SELECT a.name rName, a.id, acc.rType FROM accounts a JOIN\n")
							.append(
									"(SELECT gc.subID id, 'C' rType FROM generalcontractors gc\nJOIN facilities f ON f.opID = gc.genID AND f.corporateID =")
							.append(currPerm.getAccountId()).append(
									" GROUP BY id) AS acc on a.id = acc.id WHERE a.status != 'Deactivated')\n"); // here
				}
				if (currPerm.hasPermission(OpPerms.EditUsers)) {
					sb
							.append(
									"UNION\n(SELECT u.name rName, u.id, IF(u.isGroup='Yes','G','U') rType FROM users u JOIN\n"
											+ "((select f.opID id FROM facilities f WHERE f.corporateID =")
							.append(currPerm.getAccountId())
							.append(
									")\nUNION\n(SELECT o.id id FROM operators o WHERE o.parentID =")
							.append(currPerm.getAccountId()).append(
									")\n) AS t ON u.accountID = t.id)");
				}
				if (currPerm.hasPermission(OpPerms.ManageEmployees)) {
					sb
							.append(
									"\nUNION\n(\nSELECT CONCAT(e.firstName, ' ', e.lastName) rName, e.id, 'E' rType FROM employee e join\n"
											+ "((SELECT f.opID id FROM facilities f WHERE f.corporateID =")
							.append(currPerm.getAccountId())
							.append(
									")\nUNION\n(SELECT o.id id from operators o where o.parentID =")
							.append(currPerm.getAccountId())
							.append(")\n")
							.append(
									"UNION\n(select gc.subID FROM generalcontractors gc JOIN facilities f ON f.opID = gc.genID AND f.corporateID =")
							.append(currPerm.getAccountId()).append(
									")\n) AS rE on e.accountID = rE.id)\n");
				}
				sb
						.append(") AS r1\nON i1.foreignKey = r1.id AND i1.indexType = r1.rType");
				sql.addJoin(sb.toString());
				sb.setLength(0);
			} else if (currPerm.isOperator()) {
				sb
						.append(
								"\nJOIN ((\nSELECT a.name rName, a.id, acc.rType FROM accounts a JOIN \n")
						.append(
								"(SELECT gc.subID id, 'C' rType FROM generalcontractors gc WHERE gc.genID =")
						.append(currPerm.getAccountId()).append(
								") AS acc ON a.id = acc.id WHERE a.status != 'Deactivated'  )");
				if (currPerm.hasPermission(OpPerms.EditUsers)) {
					sb
							.append(
									"\nUNION\n(SELECT u.name rName, u.id id, if(u.isGroup='Yes','G','U') rType FROM users u WHERE u.accountID =")
							.append(currPerm.getAccountId()).append(')');
				}
				if (currPerm.hasPermission(OpPerms.ManageEmployees)) {
					sb
							.append(
									"\nUNION\n(SELECT CONCAT(e.firstName, ' ', e.lastName) rName, e.id, 'E' rType FROM employee e JOIN "
											+ "generalcontractors gc ON gc.subID = e.accountID WHERE gc.genID =")
							.append(currPerm.getAccountId()).append(")");
				}
				sb
						.append("\n) AS r1\nON i1.foreignKey = r1.id AND i1.indexType = r1.rType");
				sql.addJoin(sb.toString());
				sb.setLength(0);
			}
		}
		sql.addWhere("i1.value LIKE '" + terms.get(0) + "%'");
		if (!Strings.isEmpty(extraWhere))
			sql.addWhere(extraWhere);
		sql.addGroupBy("i1.foreignKey, i1.indexType");
		if (buildCommon) {
			sql.addOrderBy("foreignKey");
			return sql.toString();
		}
		if (orderBy != null)
			sql.addOrderBy("rName, m DESC, score, foreignKey");
		else
			sql.addOrderBy("m DESC, score, foreignKey");
		if (limit != null)
			sql.setLimit(limit);
		if (start != null)
			sql.setStartRow(start);

		return sql.toString();
	}

	public String buildAccountSearch(Permissions currPerm, List<String> terms) {
		StringBuilder sb = new StringBuilder();
		SelectSQL sql = new SelectSQL("accounts a");
		PermissionQueryBuilder pb;
		sql.addField("a.id foreignKey, a.name, case a.type when 'Contractor'"
				+ " then 'C' when 'Corporate' then 'CO' when 'Operator' then 'O'"
				+ "when 'Assessment' then 'AS' end indexType");
		sql.addOrderBy("a.name");
		for (String searchTerm : terms) {
			sb.append("(a.name LIKE '").append(searchTerm).append(
					"%' OR a.nameIndex LIKE '").append(searchTerm).append(
					"%' OR a.id = '").append(searchTerm).append("')").append(
					" OR ");
		}
		sb.setLength(sb.lastIndexOf(" OR "));
		if (currPerm != null) {
			pb = new PermissionQueryBuilder(currPerm);
			pb.setAccountAlias("a");
			sql.addWhere(sb.toString() + pb.toString());
		} else
			sql.addWhere(sb.toString());

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
		String[] terms = check.toUpperCase().split("\\s+|@");
		Collection<String> stringCollection;
		if (removeDups)
			stringCollection = new HashSet<String>(terms.length);
		else
			stringCollection = new ArrayList<String>();
		for (int i = 0; i < terms.length; i++) {
			stringCollection.add(terms[i].replaceAll(
					"^(HTTP://)(W{3})|^(HTTP://)|^(W{3}.)|\\W", "").replaceAll(
					"[^a-zA-Z0-9\\s]", ""));
		}
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
		List<String> array;
		List<BasicDynaBean> l = null;
		String commonSql = "' term, SUM(total) t FROM " + indexStats
				+ " WHERE value LIKE '";
		String commonEnd = " AND indexType IS NULL)";
		StringBuilder fullQuery = new StringBuilder();
		if (onlyValid)
			fullQuery.append("SELECT termsTable.term, termsTable.t FROM(");
		fullQuery.append("(SELECT '").append(0).append(commonSql).append(
				terms.get(0)).append("%'").append(commonEnd);
		for (int i = 1; i < terms.size(); i++) {
			fullQuery.append("\nUnion\n");
			fullQuery.append("(SELECT '").append(i).append(commonSql).append(
					terms.get(i)).append("%'").append(commonEnd);
		}
		fullQuery.append(" ORDER BY t");
		if (onlyValid)
			fullQuery
					.append(") AS termsTable\n WHERE termsTable.t IS NOT NULL");
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
