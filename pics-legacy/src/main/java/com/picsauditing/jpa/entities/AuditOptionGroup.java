package com.picsauditing.jpa.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.util.Strings;

@Entity
@Table(name = "audit_option_group")
@SuppressWarnings("serial")
public class AuditOptionGroup extends BaseTableRequiringLanguages {
	private String name;
	private boolean radio = false;
	private String uniqueCode;

	private List<AuditOptionValue> values = new ArrayList<AuditOptionValue>();
	private List<AuditQuestion> questions = new ArrayList<AuditQuestion>();

	private int maxScore = 0;
	/**
	 * Periodically, we need to query commonly used OptionValues and update this list.
	 */
	static public String[] COMMON_TYPES = new String[] { "Colors", "LowMedHigh", "YesNo", "YesNoNA", "OfficeLocation",
			"Rating" };

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

	@OneToMany(mappedBy = "group", cascade = { CascadeType.REMOVE })
	@OrderBy("number")
	public List<AuditOptionValue> getValues() {
		return values;
	}

	public void setValues(List<AuditOptionValue> values) {
		this.values = values;
	}

	@OneToMany(mappedBy = "option")
	public List<AuditQuestion> getQuestions() {
		return questions;
	}

	public void setQuestions(List<AuditQuestion> questions) {
		this.questions = questions;
	}

	/**
	 * Gets the max score from all option values linked to this type
	 */
	@Transient
	public int getMaxScore() {
		if (maxScore == 0) {
			for (AuditOptionValue value : getValues()) {
				if (maxScore < value.getScore())
					maxScore = value.getScore();
			}
		}

		return maxScore;
	}

	@Transient
	public String getIdentifier() {
		if (!Strings.isEmpty(uniqueCode))
			return uniqueCode;

		return id + "";
	}

	@Override
	@Transient
	public String getI18nKey() {
		if (!Strings.isEmpty(uniqueCode)) {
			if (uniqueCode.equals("Country") || uniqueCode.equals("CountrySubdivision") || uniqueCode.equals("YesNo")
					|| uniqueCode.equals("LowMedHigh"))
				return uniqueCode;
		}

		return getClass().getSimpleName() + "." + getIdentifier();
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
	public String getAutocompleteItem() {
		return "[" + id + "] " + name;
	}

	@Transient
	@Override
	public String getAutocompleteValue() {
		return name;
	}

	public void cascadeRequiredLanguages(List<String> add, List<String> remove) {
		return;
	}

	public boolean hasMissingChildRequiredLanguages() {
		return getLanguages().isEmpty();
	}
}