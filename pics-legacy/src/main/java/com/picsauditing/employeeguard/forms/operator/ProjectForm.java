package com.picsauditing.employeeguard.forms.operator;

import com.picsauditing.employeeguard.forms.AddAnotherForm;

import java.util.Date;

public class ProjectForm  implements AddAnotherForm {
	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private boolean addAnother;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean isAddAnother() {
		return addAnother;
	}

	@Override
	public void setAddAnother(boolean addAnother) {
	}
}
