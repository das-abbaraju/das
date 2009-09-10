package com.picsauditing.jpa.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
public class AmBest {

	public static Map<Integer, String> financialMap = new HashMap<Integer, String>() {
		{
			put(1, "I");
			put(2, "II");
			put(3, "III");
			put(4, "IV");
			put(5, "V");
			put(6, "VI");
			put(7, "VII");
			put(8, "VIII");
			put(9, "IX");
			put(10, "X");
			put(11, "XI");
			put(12, "XII");
			put(13, "XIII");
			put(14, "XIV");
			put(15, "XV");
		}
	};

	public static Map<Integer, String> ratingMap = new HashMap<Integer, String>() {
		{
			put(10, "A++");
			put(11, "A+");
			put(12, "A");
			put(13, "A-");
			put(20, "B++");
			put(21, "B+");
			put(22, "B");
			put(23, "B-");
			put(30, "C++");
			put(31, "C+");
			put(32, "C");
			put(33, "C-");
			put(41, "D");
			put(46, "E");
			put(48, "F");
			put(90, "S");
		}
	};

	private int amBestId;
	private String naic;
	private int ratingCode;
	private int financialCode;
	private String companyName;
	private String companyNameIndex;
	private String state;
	private String country;
	private Date effectiveDate;

	@Id
	@Column(nullable = false)
	public int getAmBestId() {
		return amBestId;
	}

	public void setAmBestId(int amBestId) {
		this.amBestId = amBestId;
	}

	/**
	 * National Association of Insurance Commissioners number use to uniquely
	 * identify an insurance carrier http://www.naic.org/
	 * 
	 * @return
	 */
	@Column(length = 6)
	public String getNaic() {
		return naic;
	}

	public void setNaic(String naic) {
		this.naic = naic;
	}

	public int getRatingCode() {
		return ratingCode;
	}

	public void setRatingCode(int ratingCode) {
		this.ratingCode = ratingCode;
	}

	public int getFinancialCode() {
		return financialCode;
	}

	public void setFinancialCode(int financialCode) {
		this.financialCode = financialCode;
	}

	/**
	 * 
	 * @return A.M. Best rating such as A+
	 */
	@Transient
	public String getRatingAlpha() {
		return ratingMap.get(ratingCode);
	}

	/**
	 * 
	 * @return A.M. Best financial converted to a Roman numeral
	 */
	@Transient
	public String getFinancialAlpha() {
		return financialMap.get(financialCode);
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * The index (soundex) version of the insurance company
	 * 
	 * @return
	 */
	public String getCompanyNameIndex() {
		return companyNameIndex;
	}

	public void setCompanyNameIndex(String companyNameIndex) {
		this.companyNameIndex = companyNameIndex;
	}

	@Column(length = 2)
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Column(length = 2)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Override
	public String toString() {
		return naic + " " + companyName + " R:" + ratingMap.get(ratingCode) + " F:" + financialMap.get(financialCode);
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

}
