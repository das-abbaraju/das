package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@Transactional
public class FlagOshaCriteriaDAO extends PicsDAO {

	public FlagOshaCriteria save(FlagOshaCriteria o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		remove(find(id));
	}

	public void remove(FlagOshaCriteria row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public FlagOshaCriteria find(int id) {
		return em.find(FlagOshaCriteria.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<FlagOshaCriteria> findByOperator(OperatorAccount operator) {
		Query query = em.createQuery("SELECT t FROM FlagOshaCriteria t " + "WHERE t.operatorAccount = :flagID");
		query.setParameter("flagID", operator.getInheritFlagCriteria());
		return query.getResultList();
	}

	public FlagOshaCriteria findByOperatorFlag(OperatorAccount operator, String Where) {
		if(Strings.isEmpty(Where)) 
			Where = "";
		else 
			Where = "AND "+ Where;
		try {
			Query query = em.createQuery("SELECT t FROM FlagOshaCriteria t "
					+ "WHERE t.operatorAccount = ? " + Where);
			query.setParameter(1, operator.getInheritAudits());
			return (FlagOshaCriteria) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
