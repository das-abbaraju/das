package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

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

	public void setTime(int time) {
		this.time = time;
	}
	
	public boolean isFlagged(String value) {
		if (flag.equals(YesNo.No))
			return false;
		return true;
	}

}
