package com.picsauditing.employeeguard.services;

import com.picsauditing.employeeguard.daos.ProjectCompanyDAO;
import com.picsauditing.employeeguard.entities.ProjectCompany;
import com.picsauditing.util.Strings;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

@Deprecated
public class OperatorProjectService {

	@Autowired
	private ProjectCompanyDAO projectCompanyDAO;

	public ProjectCompany getProject(String id, int accountId) {
		return projectCompanyDAO.findProject(NumberUtils.toInt(id), accountId);
	}

	public List<ProjectCompany> search(String searchTerm, int accountId) {
		if (Strings.isEmpty(searchTerm)) {
			return Collections.emptyList();
		}

		return projectCompanyDAO.search(searchTerm, accountId);
	}

}
