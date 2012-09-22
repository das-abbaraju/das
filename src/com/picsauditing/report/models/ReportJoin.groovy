package com.picsauditing.report.models;

import com.picsauditing.report.tables.ReportForeignKey
import com.picsauditing.report.tables.ReportOnClause;
import com.picsauditing.report.tables.ReportTable

public class ReportJoin {

	private String alias
	private ReportTable toTable
	public List<ReportJoin> joins = new ArrayList<ReportJoin>()
	private ReportOnClause onClause;
	private boolean required = true;

	void join(Closure[] joinNames) {
		System.out.println("calling join on " + alias);
		joinNames.each { toJoin ->
			toJoin.delegate = toTable
			ReportJoin childJoin = toJoin()
			joins.add(childJoin)
			System.out.println("join " + toTable + " to " + childJoin)
		}
	}
	
	String toString() {
		return alias;
	}
}
