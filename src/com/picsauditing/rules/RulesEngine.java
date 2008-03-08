package com.picsauditing.rules;

import java.util.List;

abstract class RulesEngine {
	protected RulesSet rs;
	
	abstract Object calculate(Object o1, Object o2, Object o3, Object o4, Object o5);

	abstract void setUp(List<RulesRowBean> rows) throws Exception;
}
