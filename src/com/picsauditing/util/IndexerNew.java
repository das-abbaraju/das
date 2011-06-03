package com.picsauditing.util;

import com.picsauditing.access.Anonymous;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.jpa.entities.Trade;

@SuppressWarnings("serial")
public class IndexerNew extends PicsActionSupport {

	private IndexerEngine engine;

	@Override
	@Anonymous
	public String execute() throws Exception {
		//engine.runAll(engine.getEntries());
		engine.run(Trade.class);

		return SUCCESS;
	}

	public void setEngine(IndexerEngine engine) {
		this.engine = engine;
	}

}
