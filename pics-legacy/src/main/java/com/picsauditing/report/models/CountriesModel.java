package com.picsauditing.report.models;

import com.picsauditing.access.Permissions;
import com.picsauditing.report.fields.Field;
import com.picsauditing.report.tables.CountryTable;

import java.util.Map;

public class CountriesModel extends AbstractModel {

	public CountriesModel(Permissions permissions) {
		super(permissions, new CountryTable());
	}

	public ModelSpec getJoinSpec() {
		ModelSpec country = new ModelSpec(null, "Country");

		return country;
	}

	@Override
	public Map<String, Field> getAvailableFields() {
		Map<String, Field> fields = super.getAvailableFields();
		Field countryName = fields.get("CountryEnglish".toUpperCase());
        if (countryName != null)
            countryName.setUrl("ManageCountries.action?country={CountryIsoCode}");

        return fields;
	}
}