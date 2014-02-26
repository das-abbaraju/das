package com.picsauditing.employeeguard.services.entity;

import java.util.List;

public interface Searchable<ENTITY> {

	List<ENTITY> search(String searchTerm, int accountId);

}
