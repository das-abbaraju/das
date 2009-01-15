package com.picsauditing.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AnswerMapByAudits {
	
	private Map<ContractorAudit, AnswerMap> data = new HashMap<ContractorAudit, AnswerMap>();
	
	public AnswerMapByAudits() {}
	
	public AnswerMapByAudits( AnswerMapByAudits toCopy ) {
		for( ContractorAudit audit : toCopy.data.keySet() ) {
			AnswerMap mapCopy = new AnswerMap(toCopy.get( audit ));
			put( audit, mapCopy );
		}
	}
	
	public AnswerMapByAudits( AnswerMapByAudits toCopy, OperatorAccount operator) {
		if( Boolean.parseBoolean("true")) {
			throw new RuntimeException( "remember to put in the precedence logic for two audits of the same type for the same contractor");	
		}

		for( ContractorAudit audit : toCopy.data.keySet() ) {
			AnswerMap mapCopy = new AnswerMap(toCopy.get( audit ), operator );
			put( audit, mapCopy );
		}
		
		Set<ContractorAudit> auditSet = new HashSet<ContractorAudit> ( data.keySet() );
		
		for( ContractorAudit audit : auditSet ) {
			for( ContractorOperator contractorOperator : operator.getContractorOperators() ) {
				if( "Y".equals( contractorOperator.getWorkStatus() ) ) {

					boolean canSee = false;
					
					//check that they can see it
					for( AuditOperator auditOperator : operator.getAudits() ) {
						if( auditOperator.getAuditType().equals(audit.getAuditType())) {
							if( auditOperator.isCanSee() ) {
								canSee = true;	
							}
							break;
						}
					}
					
					if( canSee ) {

						
					}
					else {
						remove( audit );
					}
				}
				else {
					remove( audit );
				}
			}
		}		
		
	}
	
	
	
	public AnswerMapByAudits copy() {
		AnswerMapByAudits response = new AnswerMapByAudits( this );
		return response;
	}
	
	public AnswerMapByAudits copy(OperatorAccount operator) {
		AnswerMapByAudits response = new AnswerMapByAudits( this, operator );
		return response;
	}
	
	
	
	public AnswerMap get( ContractorAudit audit ) {
		return data.get( audit );
	}
	
	public void put( ContractorAudit audit, AnswerMap answers) {
		data.put(audit, answers);
	}
	
	public void remove( ContractorAudit audit ) {
		if( audit != null ) 
			data.remove(audit);
	}

	public void resetFlagColors() {
		// TODO Auto-generated method stub
		
	}
}
