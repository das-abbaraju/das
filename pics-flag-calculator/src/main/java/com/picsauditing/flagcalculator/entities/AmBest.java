package com.picsauditing.flagcalculator.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.flagcalculator.entities.AmBest")
public class AmBest {
	private int amBestId;
	private int ratingCode;
	private int financialCode;

    @Id
    @Column(nullable = false)
    public int getAmBestId() {
        return amBestId;
    }

    public void setAmBestId(int amBestId) {
        this.amBestId = amBestId;
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
}
