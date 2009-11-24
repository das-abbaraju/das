package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

@Embeddable
public class FlagOshaCriterion {
	protected HurdleType hurdleFlag = HurdleType.None;
	protected float hurdle = 0.0f;
	protected int time = 1;

	@Enumerated(EnumType.STRING)
	public HurdleType getHurdleFlag() {
		return hurdleFlag;
	}

	public void setHurdleFlag(HurdleType hurdleFlag) {
		this.hurdleFlag = hurdleFlag;
	}

	@Transient
	public boolean isRequired() {
		if (HurdleType.NAICS.equals(hurdleFlag) || HurdleType.Absolute.equals(hurdleFlag))
			return true;
		return false;
	}

	@Column(nullable = false)
	public float getHurdle() {
		return hurdle;
	}

	public void setHurdle(float hurdle) {
		this.hurdle = hurdle;
	}

	@Column(nullable = false)
	public int getTime() {
		return time;
	}

	@Transient
	public boolean isTimeAverage() {
		return time == 3;
	}
	
	@Transient
	public boolean isLastYearOnly() {
		return time == 2;
	}
	
	public void setTime(int time) {
		this.time = time;
	}

	public boolean isFlagged(float naics, float value) {
		if (!isRequired())
			return false;

		if (HurdleType.NAICS.equals(hurdleFlag)) {
			return value > naics * hurdle / 100;
		}

		if (HurdleType.Absolute.equals(hurdleFlag))
			return value > hurdle;
		return false;
	}

	@Transient
	@Override
	public String toString() {
		return "flag:" + hurdleFlag + " time:" + time + " hurdle:" + hurdle;
	}

}
