package com.picsauditing.auditbuilder.entities;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Table(name = "ref_trade")
@SqlResultSetMapping(name = "matchingTradeResults", entities = @EntityResult(entityClass = Trade.class), columns = @ColumnResult(name = "matching"))
public class Trade extends BaseTable {

	static public final int TOP_ID = 5;
	static public final Trade TOP = new Trade(TOP_ID);

	private Trade parent = TOP;
	private LowMedHigh safetyRisk;
	private int indexLevel;
	private int indexStart;
	private int indexEnd;

	public Trade() {
	}

	public Trade(int id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "parentID")
	public Trade getParent() {
		return parent;
	}

	public void setParent(Trade parent) {
		this.parent = parent;
	}

	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	public int getIndexLevel() {
		return indexLevel;
	}

	public void setIndexLevel(int indexLevel) {
		this.indexLevel = indexLevel;
	}

	public int getIndexStart() {
		return indexStart;
	}

	public void setIndexStart(int indexStart) {
		this.indexStart = indexStart;
	}

	public int getIndexEnd() {
		return indexEnd;
	}

	public void setIndexEnd(int indexEnd) {
		this.indexEnd = indexEnd;
	}
}