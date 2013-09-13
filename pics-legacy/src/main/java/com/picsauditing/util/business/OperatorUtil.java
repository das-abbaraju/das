package com.picsauditing.util.business;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is not meant to be inherited or serialized.
 * 
 * @author AAnanighian
 *
 */
public class OperatorUtil {
	
	private static final Set<Integer> OPERATOR_IDS_FOR_INTERNAL_USE = Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(10403, 2723)));
	
	private OperatorUtil() {		
	}
	
	public static Set<Integer> operatorsIdsUsedForInternalPurposes() {
		return OPERATOR_IDS_FOR_INTERNAL_USE;
	}

}
