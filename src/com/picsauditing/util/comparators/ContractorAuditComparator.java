package com.picsauditing.util.comparators;

import java.util.Comparator;

import com.picsauditing.jpa.entities.ContractorAudit;

public class ContractorAuditComparator implements Comparator<ContractorAudit> {

	String[] orderByClauses = null;

	public ContractorAuditComparator(String... orderByClauses) {
		this.orderByClauses = orderByClauses;
	}

	@Override
	public int compare(ContractorAudit o1, ContractorAudit o2) {

		int compareValue = 0;

		for (String orderBy : orderByClauses) {
			compareValue = compareByField(orderBy, o1, o2);

			if (compareValue != 0)
				break;

		}

		return compareValue;
	}

	private String getSortField(String fieldName) {
		if (fieldName == null)
			return "createdDate";
		return fieldName;
	}

	private int getSortMultiplier(String fieldName) {
		if (fieldName == null)
			return 1;
		return Integer.parseInt(fieldName);
	}

	public int compareByField(String fieldName, ContractorAudit o1, ContractorAudit o2) {
		String[] value = getSort(fieldName);
		int comparison = 0;

		if (getSortField(value[0]).equalsIgnoreCase("createdDate")) {
			comparison = o1.getCreatedDate().compareTo(o2.getCreatedDate());
		}

		return getSortMultiplier(value[1]) * comparison;
	}

	public String[] getSort(String fieldName) {
		return fieldName.split(" ");
	}
}
