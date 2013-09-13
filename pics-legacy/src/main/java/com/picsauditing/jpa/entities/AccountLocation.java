package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vividsolutions.jts.geom.Geometry;

@SuppressWarnings("serial")
@Entity
@Table(name = "account_location")
public class AccountLocation extends BaseTable {
	
	@ManyToOne
	@JoinColumn(name = "accountID")
	private Account acccount;
	
	private Geometry location;

}