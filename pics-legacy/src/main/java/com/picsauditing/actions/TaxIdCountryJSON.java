package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class TaxIdCountryJSON extends PicsActionSupport {

    @Autowired
    private CountryDAO dao;

    @Anonymous
    public String execute() {
        String iso = getRequest().getParameter("iso_code");
        String locale = getRequest().getParameter("locale");

        JSONObject taxIdJson = new JSONObject();

        taxIdJson.put("tax_id_required", taxIdRequiredby(iso));
        taxIdJson.put("label", getTaxIdLabel(iso, locale));

        json = taxIdJson;

        return JSON;
    }

    private String getTaxIdLabel(String iso, String locale) {
        Country country = dao.findbyISO(iso);

        if (country.isBrazil()) {
            return getText(locale, "FeeClass.CNPJ");
        } else if (country.isEuropeanUnion()) {
            return getText(locale, "FeeClass.VAT");
        } else {
            return "";
        }
    }

    private boolean taxIdRequiredby(String iso) {
        if (iso.toUpperCase().equals("GB")) {
            return false;
        }
        Country country = dao.findbyISO(iso);
         return country.isEuropeanUnion() || country.isBrazil();
    }

}
