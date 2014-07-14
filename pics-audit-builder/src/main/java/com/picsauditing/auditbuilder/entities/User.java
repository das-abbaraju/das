package com.picsauditing.auditbuilder.entities;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "users")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class User extends BaseTable implements java.io.Serializable {

	public static int SYSTEM = 1;

	public User(int id) {
		this.id = id;
	}
}