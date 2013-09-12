package com.picsauditing.search;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.jpa.entities.AbstractIndexableTable;

public class SearchList {

	public List<SearchItem> data = null;

	public SearchList() {
		data = new ArrayList<SearchItem>();
	}

	public SearchItem add(SearchItem item) {
		boolean found = false;

		for (SearchItem other : data) {
			if (other.equals(item)) {
				other.record = item.record;
				found = true;
				break;
			}
		}

		if (!found) {
			data.add(item);
		}

		return item;
	}

	public List<AbstractIndexableTable> getRecordsOnly(boolean nullsAllowed) {
		List<AbstractIndexableTable> recordsOnly = new ArrayList<AbstractIndexableTable>();
		for (SearchItem item : data) {
			if (nullsAllowed) {
				recordsOnly.add(item.record);
			} else {
				if (item.record != null) {
					recordsOnly.add(item.record);
				}
			}
		}

		return recordsOnly;
	}
}
