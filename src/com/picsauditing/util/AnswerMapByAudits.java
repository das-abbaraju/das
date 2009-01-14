package com.picsauditing.util;

import java.util.HashMap;
import java.util.Map;

import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OperatorAccount;

public class AnswerMapByAudits {
	
	private Map<ContractorAudit, AnswerMap> data = new HashMap<ContractorAudit, AnswerMap>();
	
	public AnswerMapByAudits() {}
	
	private AnswerMapByAudits( AnswerMapByAudits toCopy ) {
		for( ContractorAudit audit : toCopy.data.keySet() ) {
			AnswerMap mapCopy = new AnswerMap(toCopy.get( audit ));
			put( audit, mapCopy );
		}
	}
	
	private AnswerMapByAudits( AnswerMapByAudits toCopy, OperatorAccount operator) {
		this( toCopy );
		
		
		
		
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
}
