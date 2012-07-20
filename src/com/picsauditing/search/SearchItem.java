package com.picsauditing.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.AbstractIndexableTable;

public class SearchItem {

	public int id;
	public Class<? extends AbstractIndexableTable> type = null;
	public AbstractIndexableTable record = null;

	private static final Logger logger = LoggerFactory.getLogger(SearchBox.class);

	public SearchItem(Class<? extends AbstractIndexableTable> type, int id) {
		this.type = type;
		this.id = id;
	}

	public SearchItem(Class<? extends AbstractIndexableTable> type, int id, AbstractIndexableTable record) {
		this.type = type;
		this.id = id;
		this.record = record;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof SearchItem))
			return false;

		try {
			SearchItem si = (SearchItem) o;
			if (id == si.id && type == si.type) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception in equals for SearchItem.", e);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return ((type.getName().hashCode() % 1000) * 10000000) + id;
	}
}
