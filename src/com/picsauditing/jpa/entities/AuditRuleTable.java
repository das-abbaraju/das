package com.picsauditing.jpa.entities;

import java.util.List;
import java.util.Map;

import com.picsauditing.actions.auditType.AuditRuleColumn;

public interface AuditRuleTable {
	public Map<AuditRuleColumn, List<String>> getMapping();
}
