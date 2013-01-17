package com.picsauditing.dao;

import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.FeeClass;
import com.picsauditing.jpa.entities.InvoiceFeeCountry;

public class InvoiceFeeCountryDAO extends PicsDAO {

	public List<InvoiceFeeCountry> findAllInvoiceFeeCountry(FeeClass feeClass, CountrySubdivision countrySubdivision) {
		Query query = em.createQuery("SELECT c FROM InvoiceFeeCountry c JOIN c.invoiceFee " +
				"WHERE c.invoiceFee.feeClass = :feeClass AND c.subdivision = :countrySubdivision");

		query.setParameter("feeClass", feeClass);
		query.setParameter("countrySubdivision", countrySubdivision);

		return query.getResultList();
	}

	public List<InvoiceFeeCountry> findVisibleByCountryAndFeeClassList(Country country, Set<FeeClass> feeClassList) {
		Query query = em.createQuery("SELECT c FROM InvoiceFeeCountry c JOIN c.invoiceFee i " +
				"WHERE c.country = :country AND c.invoiceFee.feeClass IN (:feeClassList) AND i.visible = 1");

		query.setParameter("country", country);
		query.setParameter("feeClassList", feeClassList);

		return query.getResultList();
	}
}
