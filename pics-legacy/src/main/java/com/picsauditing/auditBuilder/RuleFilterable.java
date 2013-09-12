package com.picsauditing.auditBuilder;

import java.util.List;

public interface RuleFilterable<R> {
	public List<R> next(RuleFilter contractor);
}