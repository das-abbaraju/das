package com.picsauditing.auditbuilder.dao;

import com.picsauditing.auditbuilder.entities.BaseDecisionTreeRule;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
public class AuditDecisionTableDAO2 extends PicsDAO {
	public <T extends BaseDecisionTreeRule> List<T> findAllRules(Class<T> clazz) {
		Query query = em.createQuery("FROM " + clazz.getName()
				+ " WHERE effectiveDate <= NOW() AND expirationDate > NOW() ORDER BY priority DESC");
		return query.getResultList();
	}
}