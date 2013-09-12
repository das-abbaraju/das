package com.picsauditing.util.pagination;

import java.util.List;

public interface Paginatable<E> {

	public abstract List<E> getPaginationResults(PaginationParameters parameters);

	public abstract int getPaginationOverallCount(PaginationParameters parameters);
}
