package com.picsauditing.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.FieldType;
import com.picsauditing.report.fields.FilterType;
import com.picsauditing.report.fields.ReportField;
import com.picsauditing.report.tables.FieldCategory;

@SuppressWarnings("serial")
@Entity
@Table(name = "contractor_operator_number")
public class ContractorOperatorNumber extends BaseTable {
	private ContractorAccount contractor;
	private OperatorAccount operator;
	private ContractorOperatorNumberType type;
	private String value;

	@ManyToOne
	@JoinColumn(name = "conID", nullable = false)
	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	@ManyToOne
	@JoinColumn(name = "opID", nullable = false)
	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	@ReportField(category = FieldCategory.AccountInformation, type = FieldType.ContractorOperatorNumberType)
	public ContractorOperatorNumberType getType() {
		return type;
	}

	public void setType(ContractorOperatorNumberType type) {
		this.type = type;
	}

	@Column(nullable = false)
	@ReportField(category = FieldCategory.AccountInformation, type = FieldType.String)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Transient
	public boolean isVisibleTo(Permissions permissions) {
		if (permissions.isContractor() || permissions.isPicsEmployee())
			return true;

		if (operator.getId() == permissions.getAccountId())
			return true;

		if (permissions.isCorporate()) {
			for (Integer operatorID : permissions.getOperatorChildren()) {
				if (operatorID == operator.getId())
					return true;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s (%s)", getOperator().getName(), getValue(), getType().name());
	}
}
