package com.picsauditing.actions.countries;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SuppressWarnings("serial")
public class ManageCountries extends PicsActionSupport {
    protected Country country;
    protected CountryContact countryContact;

    private List<Country> countries;

    @Autowired
    private CountrySubdivisionDAO countrySubdivisionDAO;
    @Autowired
    private CountryDAO countryDAO;

	private final Logger logger = LoggerFactory.getLogger(ManageCountries.class);

	public String execute() throws Exception {
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);

        if (country != null) {
            countryContact = country.getCountryContact();

            return "country";
        }
        else {
            findCountries();
        }

		return SUCCESS;
	}

	public String save() throws Exception {
        permissions.tryPermission(OpPerms.DevelopmentEnvironment);

        if (countryContact.getCountry() == null) {
            countryContact.setCountry(country);
        }
        if (countryContact.getBusinessUnit() == null) {
            BusinessUnit businessUnit = new BusinessUnit();
            businessUnit.setId(1);
            countryContact.setBusinessUnit(businessUnit);
        }
        countryContact.setAuditColumns(permissions);
        countrySubdivisionDAO.save(countryContact);
        addActionMessage("Country Saved Successfully");

        return setUrlForRedirect("ProfileEdit.action?country=" + country.getIsoCode());
	}

	private void findCountries() {
		countries = countryDAO.findAll();
		Collections.sort(countries, new Comparator<Country>() {
			public int compare(Country o1, Country o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public CountryContact getCountryContact() {
        return countryContact;
    }

    public void setCountryContact(CountryContact countryContact) {
        this.countryContact = countryContact;
    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<CountrySubdivision> getCountrySubdivisionList() {
        List<CountrySubdivision> result = new ArrayList<CountrySubdivision>();

        if (country != null) {
            result = countrySubdivisionDAO.findByCountry(country);
            Collections.sort(result, CountrySubdivision.NAME_COMPARATOR);
        }

        return result;
    }

    public List<BusinessUnit> getBusinessUnitList() {
        List<BusinessUnit> result = new ArrayList<BusinessUnit>();

        if (country != null) {
            result = countryDAO.findAllBusinessUnits();
        }

        return result;
    }
}