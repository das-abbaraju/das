package com.picsauditing.actions.rules;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.actions.auditType.AuditRuleColumn;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.jpa.entities.AuditRule;

@SuppressWarnings("serial")
public abstract class AuditRuleTableBuilder<T extends AuditRule> extends PicsActionSupport {

	protected AuditDecisionTableDAO ruleDAO;

	protected Set<AuditRuleColumn> columns;
	protected List<Map<AuditRuleColumn, List<String>>> mappedRules;
	protected List<T> rules = new ArrayList<T>();
	protected Integer id;
	protected String ruleType;

	protected Date date = new Date();

	@Override
	public String execute() throws Exception {
		setup();
		return SUCCESS;
	}

	public abstract void findRules();

	public void setup() {
		columns = new TreeSet<AuditRuleColumn>();
		mappedRules = new ArrayList<Map<AuditRuleColumn, List<String>>>();
		findRules();

		for (AuditRule rule : rules) {
			Map<AuditRuleColumn, List<String>> mappedRule = rule.getMapping();
			mappedRules.add(mappedRule);
			for (Entry<AuditRuleColumn, List<String>> entry : mappedRule.entrySet()) {
				if (entry.getValue().size() > 0)
					columns.add(entry.getKey());
			}
		}
		columns.remove(AuditRuleColumn.id);
	}

	public AuditRuleColumn getIdColumn() {
		return AuditRuleColumn.id;
	}

	public Set<AuditRuleColumn> getColumns() {
		return columns;
	}

	public List<Map<AuditRuleColumn, List<String>>> getMappedRules() {
		return mappedRules;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
