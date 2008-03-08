package com.picsauditing.rules;

import java.util.List;

public class RulesEngine {
	protected RulesSet rs;
	
	public Object calculate() {
		return null;
	}

	public void setUp(List<RulesRowBean> rows) throws Exception {
		rs = new RulesSet();
	}
}
