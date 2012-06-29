package com.picsauditing.util;

import com.picsauditing.jpa.entities.PhysicalAddress;

public class PhysicalAddressUtils {

	public static String fullAddress(PhysicalAddress streetAddress, String eolDelimiter) {
		return fullAddress(streetAddress, eolDelimiter, "US");
	}

	/**
	 * Given an object that implements the PhysicalAddress interface (e.g. any of
	 * the Accounts objects), returns the full street address as a string. If a
	 * base country is specified, and the address is in that country, then the
	 * country line is supressed.
	 * 
	 * With this method, states are always abbreviated (using the isoCode).
	 * 
	 * @param eolDelimiter
	 *            e.g. "\n" for linefeeds, "(br/)" for HTML breaks, or "; " for
	 *            in-line.
	 * @param baseCountryIsoCode
	 *            (optional) "US" is assumed if left out.
	 */
	public static String fullAddress(PhysicalAddress physicalAddress, String eolDelimiter, String baseCountryIsoCode) {
		StringBuffer lines = new StringBuffer();
		if (!Strings.isEmpty(physicalAddress.getAddress())) {
			lines.append(physicalAddress.getAddress());
			lines.append(eolDelimiter);
		}
		if (!Strings.isEmpty(physicalAddress.getAddress2())) {
			lines.append(physicalAddress.getAddress2());
			lines.append(eolDelimiter);
		}
		if (!Strings.isEmpty(physicalAddress.getAddress3())) {
			lines.append(physicalAddress.getAddress3());
			lines.append(eolDelimiter);
		}
		if (!Strings.isEmpty(physicalAddress.getCity())) {
			lines.append(physicalAddress.getCity());
			lines.append(", ");
		}
		if (physicalAddress.getState() != null) {
			lines.append(physicalAddress.getState().toString());
			lines.append(" ");
		}
		if (!Strings.isEmpty(physicalAddress.getZip())) {
			lines.append(physicalAddress.getZip());
		}
		if ((physicalAddress.getCountry() != null) && !Strings.isEmpty(baseCountryIsoCode)
				&& !baseCountryIsoCode.equals(physicalAddress.getCountry().getIsoCode())) {
			lines.append(eolDelimiter);
			lines.append(physicalAddress.getCountry().toString());

		}
		return lines.toString();
	}
	public static String shortAddress(PhysicalAddress streetAddress, String fieldDelimiter) {
		return shortAddress(streetAddress, fieldDelimiter, "US");
	}
	/**
	 * Given an object that implements the PhysicalAddress interface (e.g. any of
	 * the Accounts objects), returns the City/State/Country as a string. If a
	 * base country is specified, and the address is in that country, then the
	 * country is supressed.
	 * 
	 * With this method, states are always spelled out.
	 * 
	 * @param fieldDelimiter
	 *            e.g. "\n" for linefeeds, "(br/)" for HTML breaks, or "; " for
	 *            in-line.
	 * @param baseCountryIsoCode
	 *            (optional) "US" is assumed if left out.
	 */
	public static String shortAddress(PhysicalAddress streetAddress, String fieldDelimiter, String baseCountryIsoCode) {
		StringBuffer lines = new StringBuffer();
		boolean needDelimiter = false;
		if (!Strings.isEmpty(streetAddress.getCity())) {
			lines.append(streetAddress.getCity());
			needDelimiter = true;
		}
		if (streetAddress.getState() != null) {
			if (needDelimiter) {
				lines.append(fieldDelimiter);
			}
			lines.append(streetAddress.getState().getSimpleName());
			needDelimiter = true;
		}
		if ((streetAddress.getCountry() != null) && !Strings.isEmpty(baseCountryIsoCode)
				&& !baseCountryIsoCode.equals(streetAddress.getCountry().getIsoCode())) {
			if (needDelimiter) {
				lines.append(fieldDelimiter);
			}
			lines.append(streetAddress.getCountry().toString());
		}
		return lines.toString();

	}
}
