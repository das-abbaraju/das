package com.picsauditing.actions;

import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.jpa.entities.Country;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

public class TaxIdCountryJSON extends PicsActionSupport {

    @Autowired
    private CountryDAO dao;
    private String iso;
    private String locale;

    @Anonymous
    public String execute() {
        String iso = getRequest().getParameter("iso_code");
        String locale = getRequest().getParameter("locale");

        JSONObject taxIdJson = new JSONObject();

        taxIdJson.put("tax_id_required", taxIdRequiredby(iso));
        taxIdJson.put("tax_type", getTaxIdLabel(iso, locale).toLowerCase());

        json = taxIdJson;

        return JSON;
    }

    @Anonymous
    public String taxIdInfo() {
        JSONObject taxIdJson = new JSONObject();

        taxIdJson.put("tax_id_required", taxIdRequiredby(iso));
        taxIdJson.put("label", getTaxIdLabel(iso, locale));

        json = taxIdJson;

        return JSON;
    }

    private String getTaxIdLabel(String iso, String locale) {
        Country country = dao.findByISO(iso);


        if (country.isBrazil()) {
            return getText(new Locale(locale), "FeeClass.CNPJ");
        } else if (country.isEuropeanUnion() && !country.isUK()) {
            return getText(new Locale(locale), "FeeClass.VAT");
        } else {
            return "";
        }
    }

    private boolean taxIdRequiredby(String iso) {
        if (iso.toUpperCase().equals("GB")) {
            return false;
        }
        Country country = dao.findByISO(iso);
         return country.isEuropeanUnion() || country.isBrazil();
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
