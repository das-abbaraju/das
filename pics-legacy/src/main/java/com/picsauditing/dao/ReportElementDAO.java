package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.ReportElement;

public class ReportElementDAO extends PicsDAO {

	@Transactional(propagation = Propagation.NESTED)
	public ReportElement save(ReportElement o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(ReportElement row) {
		if (row != null) {
			em.remove(row);
		}
	}

	@Transactional(propagation = Propagation.NESTED)
	public <E extends ReportElement> int remove(Class<E> clazz, String where) {
		Query query = em.createQuery("DELETE " + clazz.getName() + " t WHERE " + where);
		return query.executeUpdate();
	}

	@Transactional(propagation = Propagation.NESTED)
	public <E extends ReportElement> void save(List<E> reportElements) {
		for (ReportElement reportElement : reportElements) {
			save(reportElement);
		}
	}

}
