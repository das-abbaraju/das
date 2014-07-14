package com.picsauditing.auditbuilder.entities;

import com.picsauditing.auditbuilder.service.AccountService;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "operators")
public class OperatorAccount extends Account {

	private OperatorAccount parent;
	private boolean primaryCorporate = false;
	private boolean inPicsConsortium = false;

	private List<Facility> corporateFacilities = new ArrayList<>();

	public OperatorAccount(String name) {
		this.name = name;
		this.type = AccountService.OPERATOR_ACCOUNT_TYPE;
		this.onsiteServices = true;
		this.offsiteServices = true;
		this.materialSupplier = true;
		this.transportationServices = true;
	}

	public boolean isPrimaryCorporate() {
		return primaryCorporate;
	}

	public void setPrimaryCorporate(boolean primaryCorporate) {
		this.primaryCorporate = primaryCorporate;
	}

	@OneToMany(mappedBy = "operator", orphanRemoval = true)
	@Where(clause = "type IS NULL")
	@Cascade(org.hibernate.annotations.CascadeType.ALL)
	public List<Facility> getCorporateFacilities() {
		return corporateFacilities;
	}

	public void setCorporateFacilities(List<Facility> corporateFacilities) {
		this.corporateFacilities = corporateFacilities;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentID", nullable = true)
	public OperatorAccount getParent() {
		return parent;
	}

	public void setParent(OperatorAccount parent) {
		this.parent = parent;
	}

	public boolean isInPicsConsortium() {
		return inPicsConsortium;
	}

	public void setInPicsConsortium(boolean inPicsConsortium) {
		this.inPicsConsortium = inPicsConsortium;
	}
}