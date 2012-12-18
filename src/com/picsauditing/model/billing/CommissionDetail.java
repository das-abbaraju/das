package com.picsauditing.model.billing;

import com.picsauditing.actions.users.UserAccountRole;

public class CommissionDetail {
	
	private String clientSite;
	private int clientSiteId;
	private String accountRepresentativeName;
	private UserAccountRole role;
	private int weight;
	private double points;
	private double revenue;
	private String serviceLevels;
	
	public String getClientSite() {
		return clientSite;
	}
	
	public void setClientSite(String clientSite) {
		this.clientSite = clientSite;
	}
	
	public int getClientSiteId() {
		return clientSiteId;
	}
	
	public void setClientSiteId(int clientSiteId) {
		this.clientSiteId = clientSiteId;
	}
	
	public String getAccountRepresentativeName() {
		return accountRepresentativeName;
	}
	
	public void setAccountRepresentativeName(String accountRepresentativeName) {
		this.accountRepresentativeName = accountRepresentativeName;
	}
	
	public UserAccountRole getRole() {
		return role;
	}
	
	public void setRole(UserAccountRole role) {
		this.role = role;
	}
	
	public int getWeight() {
		return weight;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public double getPoints() {
		return points;
	}
	
	public void setPoints(double points) {
		this.points = points;
	}
	
	public double getRevenue() {
		return revenue;
	}
	
	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}
	
	public String getServiceLevels() {
		return serviceLevels;
	}
	
	public void setServiceLevels(String serviceLevels) {
		this.serviceLevels = serviceLevels;
	}

}

