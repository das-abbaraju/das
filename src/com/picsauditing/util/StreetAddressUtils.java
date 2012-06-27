package com.picsauditing.util;

import java.util.ArrayList;
import java.util.List;

import com.ctc.wstx.util.StringUtil;
import com.picsauditing.jpa.entities.StreetAddress;

public class StreetAddressUtils {

	public static String fullAddress(StreetAddress streetAddress, String eolDelimiter) {
		return fullAddress(streetAddress, eolDelimiter, "US");
	}

	/**
	 * Given an object that implements the StreetAddress interface (e.g. any of
	 * the Accounts objects), returns the full street address as a string. If a
	 * base country is specified, and the address is in that country, then the
	 * country line is supressed.
	 * 
	 * @param eolDelimiter
	 *            e.g. "\n" for linefeeds, "(br/)" for HTML breaks, or "; " for
	 *            in-line.
	 * @param baseCountryIsoCode
	 *            (optional) "US" is assumed if left out.
	 */
	public static String fullAddress(StreetAddress streetAddress, String eolDelimiter, String baseCountryIsoCode) {
		StringBuffer lines = new StringBuffer();
		if (!Strings.isEmpty(streetAddress.getAddress())) {
			lines.append(streetAddress.getAddress());
			lines.append(eolDelimiter);
		}
		if (!Strings.isEmpty(streetAddress.getAddress2())) {
			lines.append(streetAddress.getAddress2());
			lines.append(eolDelimiter);
		}
		if (!Strings.isEmpty(streetAddress.getAddress3())) {
			lines.append(streetAddress.getAddress3());
			lines.append(eolDelimiter);
		}
		if (!Strings.isEmpty(streetAddress.getCity())) {
			lines.append(streetAddress.getCity());
			lines.append(", ");
		}
		if (streetAddress.getState() != null) {
			lines.append(streetAddress.getState().toString());
			lines.append(" ");
		}
		if (!Strings.isEmpty(streetAddress.getZip())) {
			lines.append(streetAddress.getZip());
		}
		if ((streetAddress.getCountry() != null) && !Strings.isEmpty(baseCountryIsoCode)
				&& !baseCountryIsoCode.equals(streetAddress.getCountry().getIsoCode())) {
			lines.append(eolDelimiter);
			lines.append(streetAddress.getCountry().toString());

		}
		return lines.toString();
	}

}
