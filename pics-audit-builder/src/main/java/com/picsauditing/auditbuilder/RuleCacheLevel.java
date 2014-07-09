package com.picsauditing.auditbuilder;

import java.util.LinkedHashMap;
import java.util.Map;

abstract public class RuleCacheLevel<K, V extends RuleFilterable<R>, R> implements RuleFilterable<R> {
	protected Map<K, V> data = new LinkedHashMap<>();
}