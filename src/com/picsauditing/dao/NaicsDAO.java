package com.picsauditing.dao;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.util.Strings;

@Transactional
public class NaicsDAO extends PicsDAO {

	public Naics find(String code) {
		return em.find(Naics.class, code);
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
	
	public float getIndustryAverage(boolean lwcr, Naics naics) {
		naics = getBroaderNaics(lwcr, naics);

		if (naics == null)
			return 0;
		if (lwcr)
			return naics.getLwcr();
		else
			return naics.getTrir();
	}

	public Naics getBroaderNaics(boolean lwcr, Naics naics) {
		String code = naics.getCode();
		if (Strings.isEmpty(code))
			return null;

		if ((lwcr && naics.getLwcr() > 0) || (!lwcr && naics.getTrir() > 0))
			return naics;
		else {
			Naics naics2 = find(code.substring(0, code.length() - 1));
			if (naics2 == null)
				return null;

			if ((lwcr && naics2.getLwcr() > 0) || (!lwcr && naics2.getTrir() > 0))
				return naics2;
			else
				return getBroaderNaics(lwcr, naics2);
		}
	}
}
