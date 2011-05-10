package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "audit_option_group")
@SuppressWarnings("serial")
public class AuditOptionGroup extends BaseTable {
	private String name;
	private boolean radio = false;
	private String uniqueCode;
	private List<AuditOptionValue> questionOptions = new ArrayList<AuditOptionValue>();
	/**
	 * Periodically, we need to query commonly used QuestionOptions and update this list.
	 */
	static public int[] COMMON_TYPES = new int[] { 25, 26, 30, 31, 20, 19 };

	@Column(nullable = false, length = 50)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isRadio() {
		return radio;
	}

	public void setRadio(boolean radio) {
		this.radio = radio;
	}

	public String getUniqueCode() {
		return uniqueCode;
	}

	public void setUniqueCode(String uniqueCode) {
		this.uniqueCode = uniqueCode;
	}

	@OneToMany(mappedBy = "type")
	@OrderBy("number")
	public List<AuditOptionValue> getQuestionOptions() {
		return questionOptions;
	}

	public void setQuestionOptions(List<AuditOptionValue> questionOptions) {
		this.questionOptions = questionOptions;
	}
	
	/**
	 * TODO get the child option max score and use it 
	 * @return
	 */
	@Transient
	public int getMaxScore() {
		return 0;
	}

	@Override
	@Transient
	public String getI18nKey() {
		if (uniqueCode != null && !uniqueCode.isEmpty()) {
			if (uniqueCode.equals("Country") || uniqueCode.equals("State"))
				return "global." + uniqueCode;
			if (uniqueCode.equals("YesNo") || uniqueCode.equals("LowMedHigh"))
				return uniqueCode;

			return this.getClass().getSimpleName() + "." + uniqueCode.replaceAll(" ", "");
		}

		return super.getI18nKey();
	}

	@Override
	@Transient
	public String getI18nKey(String property) {
		if (property != null && !property.isEmpty() && property.equals("name") && id > 0)
			return getI18nKey();

		return super.getI18nKey(property);
	}
	
	@Transient
	@Override
	public String getAutocompleteValue() {
		return getName();
	}
}