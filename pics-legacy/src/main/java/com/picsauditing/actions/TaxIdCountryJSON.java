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
        JSONObject vatjson = new JSONObject();
        vatjson.put("tax_id_required", taxIdRequiredby(iso));
        json = vatjson;
        return JSON;
    }

    private boolean taxIdRequiredby(String iso) {
        if (iso.toUpperCase().equals("GB")) {
            return false;
        }
        Country country = dao.findbyISO(iso);
         return country.isEuropeanUnion() || country.isBrazil();
    }

}
