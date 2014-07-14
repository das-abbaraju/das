package com.picsauditing.audits;

import java.util.List;

public interface RuleFilterable<R> {
	public List<R> next(RuleFilter contractor);
}