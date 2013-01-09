package com.picsauditing.dao;

import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.util.Strings;

public class NaicsDAO extends PicsDAO {

	public Naics find(String code) {
		return em.find(Naics.class, code);
	}
	
	private Naics findParent(String code) {
		Naics naics;
		while (code.length()>1) {
			code = code.substring(0, code.length() - 1);
			naics = em.find(Naics.class, code);
			if (naics != null) { 
				return naics;
			}
		}
		return null;
	}
	
	public boolean isValidNAICScode(String code) {
		Naics naics = find(code);
		if (naics != null)
			return true;
		return false;
	}

	public String guessNaicsCode(String naics) {
		if (Strings.isEmpty(naics))
			return "0";

		if (isValidNAICScode(naics))
			return naics;

		return guessNaicsCode(naics.substring(0, naics.length() - 1));
	}
	
	// FIXME Using a boolean argument here is a bad design -- as proven by a new, third option (getDartIndustryAverage, below).
	public float getIndustryAverage(boolean lwcr, Naics naics) {
		naics =  getBroaderNaics(lwcr,naics);

		if (naics == null)
			return 0;
		if (lwcr)
			return naics.getLwcr();
		else
			return naics.getTrir();
	}
	
	public float getDartIndustryAverage(Naics naics) {
		naics = getBroaderNaicsForDart(naics);

		if (naics == null)
			return 0;
		return naics.getDart();
	}

	// FIXME Using a boolean argument here is a bad design -- as proven by a new, third option (getBroaderNaicsForDart, below).
	public Naics getBroaderNaics(boolean lwcr, Naics naics) {
		String code = naics.getCode();
		if (Strings.isEmpty(code))
			return null;

		if ((lwcr && naics.getLwcr() > 0) || (!lwcr && naics.getTrir() > 0))
			return naics;
		Naics naics2 = findParent(code);
		if (naics2 == null)
			return null;
		
		if ((lwcr && naics2.getLwcr() > 0) || (!lwcr && naics2.getTrir() > 0))
			return naics2;
		return getBroaderNaics(lwcr,naics2);
	}

	private Naics getBroaderNaicsForDart(Naics naics) {
		String code = naics.getCode();
		if (Strings.isEmpty(code))
			return null;
		if (naics.getDart() > 0)
			return naics;
		Naics naics2 = findParent(code);
		if (naics2 == null || naics2.getDart() > 0)
			return naics2;
		
		return getBroaderNaicsForDart(naics2);
	}

}
