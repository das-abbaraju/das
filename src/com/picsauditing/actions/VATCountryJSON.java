package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.access.Anonymous;
import com.picsauditing.dao.CountryDAO;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.xml.AbstractJaxb2HttpMessageConverter;

public class VATCountryJSON extends PicsActionSupport{

    @Autowired
    private CountryDAO dao;

    @Anonymous
    public String execute() {
        String iso = getRequest().getParameter("iso_code");
        JSONObject vatjson = new JSONObject();
        vatjson.put("vat_required", vatRequiredby(iso));
        json = vatjson;
        return JSON;
    }

    private boolean vatRequiredby(String iso) {
        if (iso.toUpperCase().equals("GB")) return false;
        return dao.findbyISO(iso).requiresVAT();
    }

}
