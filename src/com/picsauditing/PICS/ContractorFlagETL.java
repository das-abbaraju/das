package com.picsauditing.PICS;

import java.util.List;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagOshaCriteria;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.util.log.PicsLogger;

public class ContractorFlagETL {
	public void execute(List<Integer> contractorList) {
		// try catch send email on Exception
		// run
	}
	
	private void run(int conID) {
		// get select FlagCriteria where id in (select id from FlagCriteriaOperator) 
		List<FlagCriteria> criteria;
		
		// See FlagCalculator2 line 263-302

		// List of contractor data to save into DB
		List<FlagCriteriaContractor> data;

		// populate data by evaluating each operator criteria
		// query pqf data
		// query osha data
		// query audit status
		// query caos
		
		// handle 3 year averages
		// Maybe we should use the ContractorAccount.getOsha and ContractorAccount.getEmr
		
		// delete from flag_criteria_con where conID = ?
		// insert "data"
		
	}
}
