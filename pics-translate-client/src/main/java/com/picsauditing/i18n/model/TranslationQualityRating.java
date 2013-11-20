package com.picsauditing.i18n.model;

/**
 * We need to keep this ordering. We are saving the ordinal values to the DB.
 * 
 * @author Lani Aung
 *
 */
public enum TranslationQualityRating {
	Bad,
	Questionable,
	Good;
	
	public static TranslationQualityRating getRatingFromOrdinal(int ordinal) {
		TranslationQualityRating[] ratings = TranslationQualityRating.values();
		
		if (ordinal >= 0 && ordinal < ratings.length)
			return ratings[ordinal];
		
		return null;
	}
}
