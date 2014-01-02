package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.SiteSkill;
import org.apache.commons.collections.CollectionUtils;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SiteSkillDAO extends BaseEntityDAO<SiteSkill> {
	public SiteSkillDAO() {
		this.type = SiteSkill.class;
	}

	public List<SiteSkill> findByAccountId(final int siteId) {
		TypedQuery<SiteSkill> query = em.createQuery("FROM SiteSkill s WHERE s.siteId = :siteId", SiteSkill.class);
		query.setParameter("siteId", siteId);
		return query.getResultList();
	}

	public List<SiteSkill> findByAccountIds(final Collection<Integer> siteIds) {
		if (CollectionUtils.isEmpty(siteIds)) {
			return Collections.emptyList();
		}

		TypedQuery<SiteSkill> query = em.createQuery("FROM SiteSkill s WHERE s.siteId IN (:siteIds)", SiteSkill.class);
		query.setParameter("siteIds", siteIds);
		return query.getResultList();
	}
}
