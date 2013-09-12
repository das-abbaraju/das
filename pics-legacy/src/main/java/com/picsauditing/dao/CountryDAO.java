package com.picsauditing.dao;

import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.BusinessUnit;
import com.picsauditing.jpa.entities.Country;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unchecked")
public class CountryDAO extends PicsDAO {

	public List<Country> findAll() {
		Query query = em.createQuery("FROM Country t");
		List<Country> list = new ArrayList<Country>();

		List<Country> results = query.getResultList();
		list.addAll(results);
		return list;
	}

	public Country find(String id) {
		return em.find(Country.class, id);
	}

	public List<Country> findByCSR(int csrID) {
		Query query = em.createQuery("FROM Country WHERE csr.id = ?");
		query.setParameter(1, csrID);

		return query.getResultList();
	}

    @Transactional(propagation = Propagation.NESTED)
    public Country save(Country o) {
        if (StringUtils.isEmpty(o.getIsoCode())) {
            em.persist(o);
        } else {
            o = em.merge(o);
        }
        return o;
    }

    public List<Country> findWhere(String where) {
		Query query = em.createQuery("FROM Country WHERE " + where);
		return query.getResultList();
	}

	public Country findbyISO(String iso) {
		return (Country) em.createQuery("FROM Country c WHERE c.isoCode = '" + iso + "'").getSingleResult();
	}

    public List<BusinessUnit> findAllBusinessUnits() {
        Query query = em.createQuery("FROM BusinessUnit t");
        List<BusinessUnit> list = new ArrayList<BusinessUnit>();

        List<BusinessUnit> results = query.getResultList();
        list.addAll(results);
        return list;
    }

}
