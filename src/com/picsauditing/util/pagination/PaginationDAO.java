package com.picsauditing.util.pagination;

import java.util.List;

import com.picsauditing.jpa.entities.BaseTable;

public interface PaginationDAO {

	public abstract List<? extends BaseTable> getPaginationResults(int offset, int limit);

	public abstract int getPaginationResultCount();
}
