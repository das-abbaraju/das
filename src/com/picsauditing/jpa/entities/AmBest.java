package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class AmBest {
	private int amBestId;
	private String naic;
	private String ratingAlpha;
	private int ratingNumeric;
	private String companyName;
	private String companyNameIndex;

	@Id
	@Column(nullable = false)
	public int getAmBestId() {
		return amBestId;
	}

	public void setAmBestId(int amBestId) {
		this.amBestId = amBestId;
	}

	/**
	 * National Association of Insurance Commissioners number
	 * use to uniquely identify an insurance carrier
	 * http://www.naic.org/
	 * @return
	 */
	@Column(length = 6)
	public String getNaic() {
		return naic;
	}

	public void setNaic(String naic) {
		this.naic = naic;
	}

	/**
	 * 
	 * @return A.M. Best rating such as A+
	 */
	public String getRatingAlpha() {
		return ratingAlpha;
	}

	public void setRatingAlpha(String ratingAlpha) {
		this.ratingAlpha = ratingAlpha;
	}

	/**
	 * 
	 * @return A.M. Best rating converted to a numeric value that can be used for comparison purposes
	 */
	public int getRatingNumeric() {
		return ratingNumeric;
	}

	public void setRatingNumeric(int ratingNumeric) {
		this.ratingNumeric = ratingNumeric;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * The index (soundex) version of the insurance company
	 * @return
	 */
	public String getCompanyNameIndex() {
		return companyNameIndex;
	}

	public void setCompanyNameIndex(String companyNameIndex) {
		this.companyNameIndex = companyNameIndex;
	}

}
