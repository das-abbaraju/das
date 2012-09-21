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
		System.out.println("calling join on " + alias + " on this " + this);
		joinNames.each {
			System.out.println("  getting foreign key on " + toTable);
			it.delegate = toTable
			ReportJoin childJoin = it()
			if (childJoin != null) {
				joins.add(childJoin)
				System.out.println("  found join " + toTable + " to " + childJoin)
			}
		}
	}
	
	String toString() {
		return alias;
	}
}
