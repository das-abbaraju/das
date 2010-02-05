package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "flag_data")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "daily")
public class FlagData extends BaseTable {

}
