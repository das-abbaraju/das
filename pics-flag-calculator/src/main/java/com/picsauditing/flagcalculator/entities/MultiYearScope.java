package com.picsauditing.flagcalculator.entities;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MultiYearScope {
	LastYearOnly,
	TwoYearsAgo,
	ThreeYearsAgo,
	ThreeYearAverage,
	ThreeYearSum;

	private static final List<MultiYearScope> LIST_INDIVIDUAL_YEAR_SCOPES = Collections.unmodifiableList(
            Arrays.asList(LastYearOnly, TwoYearsAgo, ThreeYearsAgo));

	public boolean isIndividualYearScope() {
		return (this == LastYearOnly || this == TwoYearsAgo || this == ThreeYearsAgo);
	}

    @Deprecated
	public static List<MultiYearScope> getListOfIndividualYearScopes() {
		return LIST_INDIVIDUAL_YEAR_SCOPES;
	}
}
