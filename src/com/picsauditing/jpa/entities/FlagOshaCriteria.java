package com.picsauditing.jpa.entities;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Entity
@Table(name = "flagoshacriteria")
//@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class FlagOshaCriteria extends BaseTable {
	protected OperatorAccount operatorAccount;
	protected FlagColor flagColor;
	protected FlagOshaCriterion lwcr;
	protected FlagOshaCriterion trir;
	protected FlagOshaCriterion fatalities;
	protected FlagOshaCriterion cad7;
	protected FlagOshaCriterion neer;

	@ManyToOne(optional = false)
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Column(name = "flagStatus", nullable = false)
	@Enumerated(EnumType.STRING)
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}

	@Embedded
	@AttributeOverrides( {@AttributeOverride(name = "hurdleFlag", column = @Column(name = "lwcrHurdleType")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "lwcrHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "lwcrTime")) })
	public FlagOshaCriterion getLwcr() {
		return lwcr;
	}

	public void setLwcr(FlagOshaCriterion lwcr) {
		this.lwcr = lwcr;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "hurdleFlag", column = @Column(name = "trirHurdleType")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "trirHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "trirTime")) })
	public FlagOshaCriterion getTrir() {
		return trir;
	}

	public void setTrir(FlagOshaCriterion trir) {
		this.trir = trir;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "hurdleFlag", column = @Column(name = "fatalitiesHurdleType")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "fatalitiesHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "fatalitiesTime")) })
	public FlagOshaCriterion getFatalities() {
		return fatalities;
	}

	public void setFatalities(FlagOshaCriterion fatalities) {
		this.fatalities = fatalities;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "hurdleFlag", column = @Column(name = "cad7HurdleType")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "cad7Hurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "cad7Time")) })
	public FlagOshaCriterion getCad7() {
		return cad7;
	}

	public void setCad7(FlagOshaCriterion cad7) {
		this.cad7 = cad7;
	}

	@Embedded
	@AttributeOverrides( { @AttributeOverride(name = "hurdleFlag", column = @Column(name = "neerHurdleType")),
			@AttributeOverride(name = "hurdle", column = @Column(name = "neerHurdle")),
			@AttributeOverride(name = "time", column = @Column(name = "neerTime")) })
	public FlagOshaCriterion getNeer() {
		return neer;
	}

	public void setNeer(FlagOshaCriterion neer) {
		this.neer = neer;
	}

	@Transient
	public boolean isRequired() {
		return lwcr.isRequired() || trir.isRequired() || fatalities.isRequired() || cad7.isRequired() || neer.isRequired();
	}

}
