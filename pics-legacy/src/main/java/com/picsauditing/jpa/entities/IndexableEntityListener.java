package com.picsauditing.jpa.entities;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import com.picsauditing.util.IndexerEngine;
import com.picsauditing.util.SpringUtils;

public final class IndexableEntityListener {

	@PostPersist
	@PostUpdate
	public void postIndexSave(Indexable entity) {
		IndexerEngine engine = (IndexerEngine) SpringUtils.getBean("SQLIndexerEngine");
		if (engine != null)
			engine.runSingle(entity);
	}
	
	//todo: remove
}
