package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flag_criteria_operator")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagCriteriaOperator extends BaseTable {

	/**
	 * Determine if a contractor's answer to this criteria should be flagged
	 * and if so, what color. If the contractor criteria is not the same as the operator
	 * criteria, then throw an exception.
	 * 
	 * @param contractorCriteria
	 * @return
	 */
	public FlagColor evaluate(FlagCriteriaContractor contractorCriteria) {
		
		return null;
	}

}
