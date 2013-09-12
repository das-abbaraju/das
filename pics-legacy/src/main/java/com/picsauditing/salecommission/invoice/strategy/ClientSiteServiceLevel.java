package com.picsauditing.salecommission.invoice.strategy;

import java.util.Set;

import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FeeClass;

public class ClientSiteServiceLevel {
	
	private ContractorOperator clientSite;
	private Set<FeeClass> serviceLevels;
	private double weight;
	
	public ContractorOperator getClientSite() {
		return clientSite;
	}
	
	public void setClientSite(ContractorOperator clientSite) {
		this.clientSite = clientSite;
	}
	
	public Set<FeeClass> getServiceLevels() {
		return serviceLevels;
	}
	
	public void setServiceLevels(Set<FeeClass> serviceLevels) {
		this.serviceLevels = serviceLevels;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	
}
