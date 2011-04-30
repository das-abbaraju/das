package com.picsauditing.util;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;

@SuppressWarnings("serial")
public class IndexerNew extends PicsActionSupport {

	private IndexerEngine engine;

	@Override
	@Anonymous
	public String execute() throws Exception {
		engine.runAll(engine.getEntries());

		return SUCCESS;
	}

	public void setEngine(IndexerEngine engine) {
		this.engine = engine;
	}

}
