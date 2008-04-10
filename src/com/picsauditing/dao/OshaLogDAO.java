package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaLog;

@Transactional
public class OshaLogDAO extends PicsDAO {

	public OshaLog save(OshaLog o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OshaLog row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}
	
	public OshaLog find(int id) {
        return em.find(OshaLog.class, id);
    }
	
}
