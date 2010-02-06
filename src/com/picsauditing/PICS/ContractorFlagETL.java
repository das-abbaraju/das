package com.picsauditing.PICS;

import java.util.List;

import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;

public class ContractorFlagETL {
	// get select FlagCriteria where id in (select id from FlagCriteriaOperator) 
	List<FlagCriteria> criteria;

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
