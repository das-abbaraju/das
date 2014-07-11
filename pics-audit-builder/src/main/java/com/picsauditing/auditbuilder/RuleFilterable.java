package com.picsauditing.auditbuilder;

import java.util.List;

public interface RuleFilterable<R> {
	public List<R> next(RuleFilter contractor);
}