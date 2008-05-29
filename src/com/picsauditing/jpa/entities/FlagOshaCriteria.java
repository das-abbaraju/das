package com.picsauditing.jpa.entities;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flagoshacriteria")
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE, region="daily")
public class FlagOshaCriteria {
	protected int id;
	protected OperatorAccount operatorAccount;
	protected FlagColor flagColor;
	protected FlagOshaCriterion lwcr;
	protected FlagOshaCriterion trir;
	protected FlagOshaCriterion fatalities;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "criteriaID", nullable = false)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@ManyToOne(optional=false)
	@JoinColumn(name="opID", nullable=false)
	public OperatorAccount getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(OperatorAccount operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	@Column(name = "flagStatus", nullable = false)
	public FlagColor getFlagColor() {
		return flagColor;
	}

	public void setFlagColor(FlagColor flagColor) {
		this.flagColor = flagColor;
	}
	
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

	@Transient
	public boolean isRequired() {
		return lwcr.isRequired() 
			|| trir.isRequired() 
			|| fatalities.isRequired();
	}
	
}
