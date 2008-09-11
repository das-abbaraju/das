package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.OperatorForm;
import com.picsauditing.util.FileUtils;

@Transactional
public class OperatorFormDAO extends PicsDAO {

	public OperatorForm save(OperatorForm o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OperatorForm row = find(id);
		remove(row);
	}

	public void remove(OperatorForm row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public OperatorForm find(int id) {
		return em.find(OperatorForm.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<OperatorForm> findByopID(int opID) {
		Query query = em.createQuery("SELECT o FROM OperatorForm o WHERE o.operatorAccount.id = ?");
		query.setParameter(1, opID);
		return query.getResultList();
	}

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
}
