package com.picsauditing.service;

import java.util.Date;

import com.picsauditing.jpa.entities.User;
import com.picsauditing.report.models.ModelType;

public class FieldInfo {
	private String fieldId;
    private String category;
    private String name;
	private String help;
	private boolean visible;
	private boolean filterable;
	private boolean sortable;
    private ModelType modelType;

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public boolean isSortable() {
        return sortable;
    }

    public void setSortable(boolean sortable) {
        this.sortable = sortable;
    }

    public boolean isFilterable() {
        return filterable;
    }

    public void setFilterable(boolean filterable) {
        this.filterable = filterable;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

	@Override
	public String toString() {
		return category + ": " + name + " - " + help;
	}
}