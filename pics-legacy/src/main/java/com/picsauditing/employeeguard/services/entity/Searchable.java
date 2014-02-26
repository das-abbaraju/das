package com.picsauditing.employeeguard.services.entity;

import java.util.List;

public interface Searchable<ENTITY> {

	/**
	 * Return a list of entities that match that search term, for that account.
	 *
	 * If the searchTerm is null or an Empty String, then an Empty List will be returned.
	 *
	 * @param searchTerm
	 * @param accountId
	 * @return
	 */
	List<ENTITY> search(String searchTerm, int accountId);

}
