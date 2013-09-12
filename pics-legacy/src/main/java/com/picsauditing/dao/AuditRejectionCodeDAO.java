package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.jpa.entities.AuditRejectionCode;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

public class AuditRejectionCodeDAO extends BaseTableDAO<AuditRejectionCode> {

	private static final String QUERY_REJECTION_CODE_FOR_OPERATORS = "FROM AuditRejectionCode t " +
			"WHERE t.operator.id IN (%s)";
	
	private static final String QUERY_PICS_DEFAULT_GENERIC_REJECTIONS = "FROM AuditRejectionCode t " +
			"WHERE t.operator.id = :operatorId";
	
	private static final Logger logger = LoggerFactory.getLogger(AuditRejectionCodeDAO.class);
	
	public AuditRejectionCodeDAO() {
		super(AuditRejectionCode.class);
	}
	
	public List<AuditRejectionCode> findByCaoPermissions(List<ContractorAuditOperatorPermission> caops) {
		if (CollectionUtils.isEmpty(caops)) {
			return Collections.emptyList();
		}
	
		// Fall-back logic in case no rejection reasons were found for operator, use PICS Generic Reasons
		List<AuditRejectionCode> results = findOperatorSpecificRejectionReasons(caops);
		if (CollectionUtils.isNotEmpty(results)) {
			return results;
		}
		
		return findPICSRejectionReasons();
	}
	
	@SuppressWarnings("unchecked")
	private List<AuditRejectionCode> findOperatorSpecificRejectionReasons(List<ContractorAuditOperatorPermission> caops) {
		List<AuditRejectionCode> results = Collections.emptyList();
		
		try {
			String parameterizedQuery = String.format(QUERY_REJECTION_CODE_FOR_OPERATORS, 
					Strings.implode(getCaopOperatorIds(caops)));
			Query query = em.createQuery(parameterizedQuery);		
			results = query.getResultList();
		} catch (Exception e) {
			logger.error("Error while looking up Operator Rejection Reasons", e);
		}
		
		return results;
	}
	
	@SuppressWarnings("unchecked")
	private List<AuditRejectionCode> findPICSRejectionReasons() {
		List<AuditRejectionCode> results = Collections.emptyList();
		
		try {
			Query query = em.createQuery(QUERY_PICS_DEFAULT_GENERIC_REJECTIONS);
			query.setParameter("operatorId", OperatorAccount.PicsConsortium);
			results = query.getResultList();
		} catch (Exception e) {
			logger.error("Error while looking up PICS Rejection Reasons", e);
		}
		
		return results;
	}
	
	private List<Integer> getCaopOperatorIds(List<ContractorAuditOperatorPermission> caops) {
		List<Integer> operatorIds = new ArrayList<Integer>();
		for (ContractorAuditOperatorPermission caop : caops) {
			operatorIds.add(caop.getOperator().getId());
		}
		
		return operatorIds;
	}

}
