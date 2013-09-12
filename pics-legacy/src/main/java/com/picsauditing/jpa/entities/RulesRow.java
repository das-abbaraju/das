package com.picsauditing.jpa.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rules_row")
public class RulesRow {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int rowID = 0;
	protected String tableName = "";
	protected int sequence = 10;
	protected String notes;
	protected String resultValue;
	protected String operator1;
	protected String value1;
	protected String operator2;
	protected String value2;
	protected String operator3;
	protected String value3;
	protected String operator4;
	protected String value4;
	protected String operator5;
	protected String value5;

	public int getRowID() {
		return rowID;
	}

	public void setRowID(int rowID) {
		this.rowID = rowID;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getResultValue() {
		return resultValue;
	}

	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}

	public String getOperator1() {
		return operator1;
	}

	public void setOperator1(String operator1) {
		this.operator1 = operator1;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getOperator2() {
		return operator2;
	}

	public void setOperator2(String operator2) {
		this.operator2 = operator2;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public String getOperator3() {
		return operator3;
	}

	public void setOperator3(String operator3) {
		this.operator3 = operator3;
	}

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public String getOperator4() {
		return operator4;
	}

	public void setOperator4(String operator4) {
		this.operator4 = operator4;
	}

	public String getValue4() {
		return value4;
	}

	public void setValue4(String value4) {
		this.value4 = value4;
	}

	public String getOperator5() {
		return operator5;
	}

	public void setOperator5(String operator5) {
		this.operator5 = operator5;
	}

	public String getValue5() {
		return value5;
	}

	public void setValue5(String value5) {
		this.value5 = value5;
	}



	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + rowID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final RulesRow other = (RulesRow) obj;
		if (rowID != other.rowID)
			return false;
		return true;
	}






}
