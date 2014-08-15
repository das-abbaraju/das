package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@SuppressWarnings("serial")
@Entity(name = "com.picsauditing.auditbuilder.entities.ContractorAccount")
@Table(name = "contractor_info")
public class ContractorAccount extends Account {

    private boolean safetySensitive;
	private LowMedHigh safetyRisk = LowMedHigh.None;
	private LowMedHigh productRisk = LowMedHigh.None;
    private LowMedHigh transportationRisk = LowMedHigh.None;
	private boolean soleProprietor;
	private AccountLevel accountLevel = AccountLevel.Full;

	private List<ContractorAudit> audits = new ArrayList<>();
	private List<ContractorOperator> operators = new ArrayList<>();
	private Set<ContractorTrade> trades = new TreeSet<>();

	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
	@Where(clause = "expiresDate > NOW() OR expiresDate IS NULL")
	public List<ContractorAudit> getAudits() {
		return this.audits;
	}

	public void setAudits(List<ContractorAudit> audits) {
		this.audits = audits;
	}

	@OneToMany(mappedBy = "contractorAccount", cascade = {CascadeType.REMOVE, CascadeType.REFRESH})
	public List<ContractorOperator> getOperators() {
		return this.operators;
	}

	public void setOperators(List<ContractorOperator> operators) {
		this.operators = operators;
	}

    @Column(name = "safetySensitive", nullable = false)
    public boolean isSafetySensitive() {
        return safetySensitive;
    }

    public void setSafetySensitive(boolean safetySensitive) {
        this.safetySensitive = safetySensitive;
    }

    @Column(name = "safetyRisk", nullable = false)
	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	@Column(name = "productRisk", nullable = false)
	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

    @Column(name = "transportationRisk", nullable = false)
    public LowMedHigh getTransportationRisk() {
        return transportationRisk;
    }

    public void setTransportationRisk(LowMedHigh transportationRisk) {
        this.transportationRisk = transportationRisk;
    }

	@OneToMany(mappedBy = "contractor")
	public Set<ContractorTrade> getTrades() {
		return trades;
	}

	public void setTrades(Set<ContractorTrade> trades) {
		this.trades = trades;
	}

	public boolean getSoleProprietor() {
		return soleProprietor;
	}

	public void setSoleProprietor(boolean soleProprietor) {
		this.soleProprietor = soleProprietor;
	}

	@Enumerated(EnumType.STRING)
	public AccountLevel getAccountLevel() {
		return accountLevel;
	}

	public void setAccountLevel(AccountLevel accountLevel) {
		this.accountLevel = accountLevel;
	}
}