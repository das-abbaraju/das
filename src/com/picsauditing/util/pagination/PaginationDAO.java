package com.picsauditing.util.pagination;

import java.util.List;

public interface PaginationDAO<E> {

	public abstract List<E> getPaginationResults(PaginationParameters parameters);

	public abstract int getPaginationResultCount(PaginationParameters parameters);
	
}
