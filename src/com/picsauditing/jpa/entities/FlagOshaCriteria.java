package com.picsauditing.jpa.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "flagoshacriteria")
public class FlagOshaCriteria extends FlagCriteria {
	protected FlagOshaCriterion lwcr;
	protected FlagOshaCriterion trir;
	protected FlagOshaCriterion fatalities;

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "flag", column = @Column(name = "flaglwcr")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "lwcrHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "lwcrTime")) })
	public FlagOshaCriterion getLwcr() {
		return lwcr;
	}

	public void setLwcr(FlagOshaCriterion lwcr) {
		this.lwcr = lwcr;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "flag", column = @Column(name = "flagtrir")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "trirHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "trirTime")) })
	public FlagOshaCriterion getTrir() {
		return trir;
	}

	public void setTrir(FlagOshaCriterion trir) {
		this.trir = trir;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "flag", column = @Column(name = "flagfatalities")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "fatalitiesHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "fatalitiesTime")) })
	public FlagOshaCriterion getFatalities() {
		return fatalities;
	}

	public void setFatalities(FlagOshaCriterion fatalities) {
		this.fatalities = fatalities;
	}

}
