package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class FlagOshaCriterion {
	protected YesNo flag;
	protected float hurdle;
	protected int time;

	@Column(nullable=false)
	public YesNo getFlag() {
		return flag;
	}

	public void setFlag(YesNo flag) {
		this.flag = flag;
	}
	
	@Transient
	public boolean isRequired() {
		return YesNo.Yes.equals(flag);
	}

	@Column(nullable=false)
	public float getHurdle() {
		return hurdle;
	}

	public void setHurdle(float hurdle) {
		this.hurdle = hurdle;
	}

	@Column(nullable=false)
	public int getTime() {
		return time;
	}
	
	@Transient
	public boolean isTimeAverage(){
		return time == 3;
	}
	
	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean isFlagged(float value) {
		if (!isRequired() || isTimeAverage())
			return false;
		return value > hurdle;
	}

}
