package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("unchecked")
public class OperatorFormDAO extends PicsDAO {
	@Transactional(propagation = Propagation.NESTED)
	public OperatorForm save(OperatorForm o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		OperatorForm row = find(id);
		remove(row);
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(OperatorForm row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorForm find(int id) {
		return em.find(OperatorForm.class, id);
	}

	
	public List<OperatorForm> findByopID(int opID) {
		Query query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.account.id = ?");
		query.setParameter(1, opID);
		return query.getResultList();
	}

	public List<OperatorForm> findTopByOpID(int opID) {
		Query query;
		if (opID == 0) {
			query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.parent is NULL ORDER BY o.account.name, o.formName");
		} else {
			query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.account.id = ? and o.parent is NULL ORDER BY o.account.name, o.formName");
			query.setParameter(1, opID);
		}
		return query.getResultList();
	}

	@Transactional(propagation = Propagation.NESTED)
	public boolean deleteOperatorForms(int opID, String ftpDir) {
		List<OperatorForm> opList = findByopID(opID);
		String path = ftpDir + "/forms/";
		for (OperatorForm operatorForms : opList) {
			if (!FileUtils.deleteFile(path + operatorForms.getFile()))
				return false;
			remove(operatorForms);
		}
		return true;
	}

	public List<OperatorForm> findByOperators(Collection<Integer> operatorIds) {
		Query query;
		
		if (operatorIds == null || operatorIds.size() == 0) {
			query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.parent is NULL ORDER BY o.account.name, o.formName");
		} else {
			query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.account.id IN (" + Strings.implode(operatorIds, ",") + ") and o.parent is NULL ORDER BY o.account.name, o.formName");
		}
		return query.getResultList();
	}

	public List<OperatorForm> findChildrenByOperators(Collection<Integer> operatorIds) {
		Query query;
		
		if (operatorIds == null || operatorIds.size() == 0) {
			return new ArrayList<OperatorForm>();
		} else {
			query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.parent.id IN (" + Strings.implode(operatorIds, ",") + ") ORDER BY o.account.name, o.formName");
		}
		return query.getResultList();
	}

}
