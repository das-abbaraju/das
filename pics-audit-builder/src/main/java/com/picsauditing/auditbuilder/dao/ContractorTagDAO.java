package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.ContractorTag;
import com.picsauditing.auditbuilder.util.Strings;

import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class ContractorTagDAO extends PicsDAO {
	public List<ContractorTag> getContractorTags(int conID, Collection<Integer> tagIDs) {
		Query query = em.createQuery("FROM com.picsauditing.auditbuilder.entities.ContractorTag WHERE contractor.id = ? AND tag.id IN ("
				+ Strings.implode(tagIDs) + ")");
		query.setParameter(1, conID);

		return query.getResultList();
	}
}