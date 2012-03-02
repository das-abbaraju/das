package com.picsauditing.util.comparators;

import java.util.Comparator;

import com.picsauditing.jpa.entities.ContractorAudit;

public class ContractorAuditComparator implements Comparator<ContractorAudit> {

	String[] orderByClauses = null;

	public ContractorAuditComparator(String... orderByClauses) {
		this.orderByClauses = orderByClauses;
	}

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
			return "creationDate";
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

		if (getSortField(value[0]).equalsIgnoreCase("creationDate")) {
			comparison = o1.getCreationDate().compareTo(o2.getCreationDate());
		}

		if (getSortField(value[0]).equalsIgnoreCase("auditFor")) {
			if(o1.getAuditFor() == null)
				return 0;
			if(o2.getAuditFor() == null)
				return 0;
			comparison = o1.getAuditFor().compareTo(o2.getAuditFor());
		}

		
		return getSortMultiplier(value[1]) * comparison;
	}

	public String[] getSort(String fieldName) {
		return fieldName.split(" ");
	}
}
