package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.springframework.util.CollectionUtils;

import com.picsauditing.jpa.entities.AuditRejectionCode;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.util.Strings;

public class AuditRejectionCodeDAO extends BaseTableDAO<AuditRejectionCode> {

	private static final String QUERY_REJECTION_CODE_FOR_OPERATORS = "FROM AuditRejectionCode t " +
			"WHERE t.operator.id IN (%s)";
	
	public AuditRejectionCodeDAO() {
		super(AuditRejectionCode.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<AuditRejectionCode> findByCaoPermissions(List<ContractorAuditOperatorPermission> caops) {
		if (CollectionUtils.isEmpty(caops)) {
			return Collections.emptyList();
		}
		
		String parameterizedQuery = String.format(QUERY_REJECTION_CODE_FOR_OPERATORS, 
				Strings.implode(getCaopOperatorIds(caops)));
		Query query = em.createQuery(parameterizedQuery);		
		return query.getResultList();
	}
	
	private List<Integer> getCaopOperatorIds(List<ContractorAuditOperatorPermission> caops) {
		List<Integer> operatorIds = new ArrayList<Integer>();
		for (ContractorAuditOperatorPermission caop : caops) {
			operatorIds.add(caop.getOperator().getId());
		}
		
		return operatorIds;
	}

}
