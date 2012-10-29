package com.picsauditing.PICS;

import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.util.CollectionUtils;

import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.SpringUtils;

/**
 * A set of generic Utilities. We should consider moving this into the Strings
 * class
 */
public class Utilities {

	public static boolean isEmptyArray(Object[] array) {
		if (ArrayUtils.isEmpty(array))
			return true;

		for (Object object : array)
			if (object == null)
				return true;

		return false;
	}

	public static String escapeHTML(String value) {
		return escapeHTML(value,Integer.MAX_VALUE);
	}

	/**
	 * This variation of escapeHTML(text) accepts a second argument of
	 * maxLength. It truncates the text to the given length, before escaping it.
	 * This means that we can safely truncate the text without worrying about
	 * truncating in the middle of an escape sequence.
	 * 
	 * Additionally, if the text is truncated, then "..." is appended in place
	 * of the truncted text.
	 */
	public static String escapeHTML(String value,int maxLength) {
		int maxIndex = Math.min(maxLength,value.length());
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < maxIndex; i++) {
			char c = value.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '\'') {
				out.append("&#" + (int) c + ";");
			} else if (c == '\n') {
				out.append("<br/>");
			} else {
				out.append(c);
			}
		}
		if (maxLength < value.length()) {
			out.append("...");
		}
		return out.toString();
	}

	public static String escapeNewLines(String value) {
		if (value == null)
			return "";
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			if ('\n' == value.charAt(i))
				temp.append("<br>");
			else
				temp.append(value.charAt(i));
		}
		return temp.toString();
	}

	public static String getBGColor(int count) {
		if ((count % 2) == 0)
			return " bgcolor=\"#FFFFFF\"";
		else
			return "";
	}

	@Deprecated
	public static float getAverageEMR(String year1, String year2, String year3, String year4) {
		Float rateFloat = 0.0f;
		int count = 0;
		if (convertToFloat(year1) > 0) {
			rateFloat += convertToFloat(year1);
			count++;
		}
		if (convertToFloat(year2) > 0) {
			rateFloat += convertToFloat(year2);
			count++;
		}
		if (convertToFloat(year3) > 0) {
			rateFloat += convertToFloat(year3);
			count++;
		}
		if (count < 3 && convertToFloat(year4) > 0) {
			rateFloat += convertToFloat(year4);
			count++;
		}

		Float avgRateFloat = rateFloat / count;
		return (float) Math.round(1000 * avgRateFloat) / 1000;
	}

	@Deprecated
	public static float convertToFloat(String year1) {
		if (year1 == null)
			return 0.0f;
		return Float.valueOf(year1).floatValue();
	}

	/**
	 * Deprecated in favor of the DateBean 
	 */
	@Deprecated
	public static Date getYesterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
	
	// TODO: Remove this from the Utilities class.  This is not a general
	//       purpose utility.
	public static float getIndustryAverage(boolean lwcr, ContractorAccount contractor) {
        float answer = 0f;
//        ContractorTrade trade = contractor.getTopTrade();
        
        if (!lwcr) {
// TODO swap this for below               answer = trade.getTrade().getNaicsTRIRI();
        	Naics naics = contractor.getNaics();
            answer = naics.getTrir();
            SelectSQL select = new SelectSQL("ref_trade_alt rta");
            select.addJoin("join ref_trade rt on rta.tradeID = rt.id");

            select.addField("rt.naicsTRIR");
            select.addField("rt.naicsLWCR");
            select.addWhere("rta.category = 'NAICS'");
            select.addWhere("rta.name=" + naics.getCode());

            try {
				Database db = new Database();

                    List<BasicDynaBean> results = db.select(select.toString(),
                                    false);
                    if (results != null && results.size() > 0) {
                            BasicDynaBean row = results.get(0);
                            answer = Database.toFloat(row, "naicsTRIR");
                    }
                    if (answer == 0f)
                    {
                            NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
                            answer = naicsDAO.getIndustryAverage(lwcr, naics);
                    }
            } catch (Exception e) {
                    NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
                    answer = naicsDAO.getIndustryAverage(lwcr, naics);
            }
        } else {
//TODO swap this for code below                answer = trade.getTrade().getNaicsLWCRI();
        	Naics naics = contractor.getNaics();
            answer = naics.getLwcr();
            NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
            answer = naicsDAO.getIndustryAverage(lwcr, naics);
        }
        return answer;
}
	
	// TODO: Remove from the Utilities class. This is not a general purpose
	//       Utility.
	public static float getDartIndustryAverage(Naics naics) {
		NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
		return naicsDAO.getDartIndustryAverage(naics);
	}
	
	/**
	 * Only to be used with smaller collections.  There will be a performance bottle neck when used on larger collections.
	 */
	public static <T> boolean collectionsAreEqual(Collection<T> collection1, Collection<T> collection2, Comparator<T> comparator) {
		if (CollectionUtils.isEmpty(collection1) || CollectionUtils.isEmpty(collection2)) {
			return false;
		}
		
		if (collection1.size() != collection2.size()) {
			return false;
		}
		
		for (T object : collection1) {
			boolean foundMatch = false;
			for (T objectForComparison : collection2) {
				if (comparator.compare(object, objectForComparison) == 0) {
					foundMatch = true;
					break;
				}
			}
			
			if (!foundMatch) {
				return false;
			}
		}
		
		return true;
	}
	
}
