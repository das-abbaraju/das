package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategoryRule;

@Transactional
@SuppressWarnings("unchecked")
public class AuditDecisionTableDAO extends PicsDAO {
	public AuditCategoryRule findAuditCategoryRule(int id) {
		return em.find(AuditCategoryRule.class, id);
	}

	public List<AuditCategoryRule> getLessGranular(AuditCategoryRule rule) {
		String where = "WHERE a.priority < " + rule.getPriority();
		if (rule.getAuditType() == null)
			where += " AND auditTypeID IS NULL";
		if (rule.getAuditCategory() == null)
			where += " AND catID IS NULL";
		
		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY a.priority");
		return query.getResultList();
	}

	public List<AuditCategoryRule> getMoreGranular(AuditCategoryRule rule) {
		String where = "WHERE a.priority > " + rule.getPriority();
		
		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY a.priority");
		return query.getResultList();
	}

	public List<AuditCategoryRule> getSimilar(AuditCategoryRule rule) {
		String where = "WHERE a.priority = " + rule.getPriority();
		
		Query query = em.createQuery("SELECT a FROM AuditCategoryRule a " + where + " ORDER BY a.priority");
		return query.getResultList();
	}
}
