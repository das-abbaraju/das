package com.picsauditing.jpa.entities;

import java.util.ArrayList;

import javax.persistence.Transient;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;

public enum FlagColor implements Translatable {

	Green("#339900", "Approve"),
	Amber("#FFCC33", "Review"),
	Red("#CC0000", "Reject"),
	Clear("#FFFFFF", "Not Applicable");

	private String hex;
	private String insuranceStatus;

	private FlagColor(String hex, String insuranceStatus) {
		this.hex = hex;
		this.insuranceStatus = insuranceStatus;
	}

	public static ArrayList<String> getValuesWithDefault() {
		ArrayList<String> values = new ArrayList<String>();
		for (FlagColor value : FlagColor.values())
			values.add(value.name());
		return values;
	}

	public String getHex() {
		return hex;
	}

	public String getInsuranceStatus() {
		return insuranceStatus;
	}

	public String getBigIcon() {
		I18nCache cache = I18nCache.getInstance();
		String title = cache.getText(getI18nKey(), TranslationActionSupport.getLocaleStatic());
		if (this == FlagColor.Clear)
			title = cache.getText(getI18nKey("insuranceStatus"), TranslationActionSupport.getLocaleStatic());
		return "<img src=\"images/icon_" + this.toString().toLowerCase()
				+ "FlagBig.gif\" width=\"32\" height=\"32\" border=\"0\" title=\"" + title + "\" />";
	}

	public String getSmallIcon() {
		I18nCache cache = I18nCache.getInstance();
		String title = cache.getText(getI18nKey(), TranslationActionSupport.getLocaleStatic());

		if (this == FlagColor.Clear)
			title = cache.getText(getI18nKey("insuranceStatus"), TranslationActionSupport.getLocaleStatic());
		return "<img src=\"images/icon_" + this.toString().toLowerCase()
				+ "Flag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"" + title + "\" />";
	}

	public String getSmallIcon(String columnName) {
		I18nCache cache = I18nCache.getInstance();
		String title = cache.getText(getI18nKey(), TranslationActionSupport.getLocaleStatic());
		if (columnName!=null)
			title = columnName+": "+title;
		if (this == FlagColor.Clear)
			title = cache.getText(getI18nKey("insuranceStatus"), TranslationActionSupport.getLocaleStatic());
		return "<img src=\"images/icon_" + this.toString().toLowerCase()
				+ "Flag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"" + title + "\" />";
	}

	static public String getSmallIcon(String flagColor, String columnName) {
		return valueOf(flagColor).getSmallIcon(columnName);
	}

	public static FlagColor getWorseColor(FlagColor color1, FlagColor color2) {
		if (color2 == null)
			return color1;

		if (color1 == null) {
			return color2;
		}

		if (color2.ordinal() > color1.ordinal())
			color1 = color2;
		return color1;
	}

	public boolean isRedAmber() {
		return this.equals(Red) || this.equals(Amber);
	}

	public boolean isRed() {
		return this.equals(Red);
	}

	public boolean isAmber() {
		return this.equals(Amber);
	}

	public boolean isGreen() {
		return this.equals(Green);
	}

	public boolean isClear() {
		return this.equals(Clear);
	}

	public boolean isWorseThan(FlagColor flagColor2) {
		if (flagColor2 == null)
			return true;

		if (this.compareTo(flagColor2) < 0)
			return true;

		return false;
	}

	@Transient
	@Override
	public String getI18nKey() {
		return this.getClass().getSimpleName() + "." + this.name();
	}

	@Transient
	@Override
	public String getI18nKey(String property) {
		return getI18nKey() + "." + property;
	}
}
