package com.picsauditing.employeeguard.daos;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.SiteSkill;

import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

public class SiteSkillDAO extends AbstractBaseEntityDAO<SiteSkill> {

	public SiteSkillDAO() {
		this.type = SiteSkill.class;
	}

	public List<SiteSkill> findByAccountId(final int siteId) {
		TypedQuery<SiteSkill> query = em.createQuery("FROM SiteSkill s " +
				"WHERE s.siteId = :siteId", SiteSkill.class);

		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<SiteSkill> findByAccountIds(final Collection<Integer> siteIds) {
		TypedQuery<SiteSkill> query = em.createQuery("FROM SiteSkill s " +
				"WHERE s.siteId IN (:siteIds)", SiteSkill.class);

		query.setParameter("siteIds", siteIds);

		return query.getResultList();
	}

	public List<AccountSkill> findReqdSkillsForAccount(int siteId) {
		TypedQuery<AccountSkill> query = em.createQuery("select skill FROM SiteSkill ss " +
						" join ss.skill skill " +
						" WHERE ss.siteId = :siteId", AccountSkill.class);

		query.setParameter("siteId", siteId);

		return query.getResultList();
	}

	public List<AccountSkill> findAllParentCorpSiteRequiredSkills(List<Integer> parentIds) {
		TypedQuery<AccountSkill> query = em.createQuery("select skill FROM SiteSkill ss " +
						" join ss.skill skill " +
						" WHERE ss.siteId IN (:parentIds)", AccountSkill.class);

		query.setParameter("parentIds", parentIds);

		return query.getResultList();
	}

}
