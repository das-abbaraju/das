package com.picsauditing.actions.report;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.search.SelectFilter;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.ReportFilterRuleHistory;

@SuppressWarnings("serial")
public class ReportRuleHistory extends ReportActionSupport {

	private SelectSQL sql;
	private AuditDecisionTableDAO auditRuleDAO;
	private ReportFilterRuleHistory filter = new ReportFilterRuleHistory();
	private int limit = 100;

	private final Map<String, String> statusTypes = new LinkedHashMap<String, String>() {
		{
			put("Deleted", "expirationDate");
			put("New", "creationDate");
			put("Updated", "updateDate");
		}
	};
	private final Map<String, String> tblNames = new HashMap<String, String>() {
		{
			put("audit_type_rule", "AuditTypeRule");
			put("audit_category_rule", "CategoryRule");
		}
	};

	public ReportRuleHistory(AuditDecisionTableDAO auditRuleDAO) {
		this.auditRuleDAO = auditRuleDAO;
	}

	public String execute() throws Exception {
		buildQuery();
		addFilterToSQL();
		run(sql);
		
		return SUCCESS;
	}

	protected void buildQuery() {
		StringBuilder sb = new StringBuilder("(");
		for (String status : statusTypes.keySet()) {
			String endClause = "", whoClause = "";
			if ("Deleted".equals(status)) {
				whoClause = ", updatedBy who";
				endClause = " WHERE expirationDate < NOW()";
			} else if ("Updates".equals(status))
				whoClause = ", updatedBy who";
			else
				whoClause = ", createdBy who";
			for (String table : tblNames.keySet()) {
				sb.append("(SELECT id,'").append(tblNames.get(table)).append(
						"' rType,'").append(status).append("' status,").append(
						statusTypes.get(status)).append(" sDate").append(
						whoClause).append(" FROM ").append(table);
				if (!endClause.isEmpty())
					sb.append(endClause);
				sb.append(")\nUNION\n");
			}
		}
		sb.setLength(sb.lastIndexOf("\nUNION\n"));
		sb.append(") AS rules");
		sql = new SelectSQL(sb.toString());
		sql.addField("rules.id, rules.rType, rules.sDate, rules.status, u.name who");
		sql.addJoin("JOIN users u ON u.id = rules.who");
		sql.addGroupBy("rules.sDate");
		sql.addOrderBy("rules.sDate DESC, rules.id");
	}
	
	public void addFilterToSQL() throws Exception {
		if (filterOn(filter.getChangedBy()))
			report.addFilter(new SelectFilter("changedBy",
					"u.name LIKE '%?%'", filter.getChangedBy()));
		if (filterOn(filter.getFindStatus()))
			report.addFilter(new SelectFilter("ruleStatus", "rules.status = '?'",
					filter.getFindStatus()));
		if (filterOn(filter.getFindType()))
			report.addFilter(new SelectFilter("ruleType", "rules.rType= '?'",
					filter.getFindType()));
		if (filterOn(filter.getFromDate()))
			report.addFilter(new SelectFilter("fromDate",
					"rules.sDate >= '? 00:00:00'", String.valueOf(DateBean
							.toDBFormat(filter.getFromDate()))));
		if (filterOn(filter.getToDate()))
			report.addFilter(new SelectFilter("toDate",
					"rules.sDate <= '? 00:00:00'", String.valueOf(DateBean
							.toDBFormat(filter.getToDate()))));
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public ReportFilterRuleHistory getFilter() {
		return filter;
	}

	public void setFilter(ReportFilterRuleHistory filter) {
		this.filter = filter;
	}

}