package com.picsauditing.struts.controller;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.ContractorLocationDao;
import com.picsauditing.persistence.model.ContractorLocation;
import com.picsauditing.search.SelectSQL;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;

public class ConvertToLatLong extends ReportActionSupport {
    @Autowired
    private ContractorLocationDao contractorLocationDao;

    public static final JSONParser JSON_PARSER = new JSONParser();

    public String execute() throws SQLException, ParseException, IOException {
        Double lng = new Double(0);
        Double lat = new Double(0);
        SelectSQL sql = new SelectSQL("accounts a");
        buildQuery(sql);

        run(sql);

        for (BasicDynaBean datum : getData()) {
            String address = null;
            int conId = 0;
            try {
                conId = (Integer) datum.get("id");
                address = parseAddress(datum);
            } catch (Exception e) {

            }

            if (address != null && !address.isEmpty()) {
                String body = null;
                try {
                    body = sendRequestToTheGoogles(address);
                } catch (IOException e) {

                }

                if (body != null && !body.isEmpty()) {
                    JSONObject location = findLocationObject(body);

                    if (location != null) {
                        lng = (Double) location.get("lng");
                        lat = (Double) location.get("lat");

                        if (lng != null && lat != null) {
                            System.out.println(address);

                        }
                    }
                }
            }
            persistToDatabase(conId, lng, lat);
        }

        return BLANK;
    }

    private void persistToDatabase(int conId, Double lng, Double lat) {
        System.out.println("(" + lat + "," + lng + ")");
        System.out.println("=========");

        ContractorLocation contractorLocation = ContractorLocation.createFrom(conId, lat.floatValue(), lng.floatValue(), 38586);
        contractorLocationDao.insertContractorLocation(contractorLocation);
    }

    private JSONObject findLocationObject(String body) throws ParseException {
        JSONObject geometry;
        try {
            JSONObject object = (JSONObject) JSON_PARSER.parse(body);

            JSONArray results = (JSONArray) object.get("results");

            JSONObject firstResult = (JSONObject) results.get(0);
            geometry = (JSONObject) firstResult.get("geometry");
        } catch (Exception e) {
            return null;
        }
        return (JSONObject) geometry.get("location");
    }

    private String sendRequestToTheGoogles(String address) throws IOException {
        String body;
        URL url = new URL(
                "https://maps.googleapis.com/maps/api/geocode/json?address="
                        + address
                        + "&sensor=false&key=AIzaSyA6_NbYqni-Oqn5LU8JEUFXkK_LG5e3gz4"
        );
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        body = IOUtils.toString(in, encoding);
        return body;
    }

    private String parseAddress(BasicDynaBean datum) {
        String address;
        address =
                ((String) datum.get("address")).replace(" ", "+") +
                        "+" + ((String) datum.get("city")).replace(" ", "+") +
                        "+" + ((String) datum.get("countrySubdivision")).replace("US-", "") +
                        "+" + datum.get("zip") +
                        "+" + datum.get("country");
        return address;
    }

    private void buildQuery(SelectSQL sql) {
        sql.addJoin("LEFT OUTER JOIN contractor_location cl ON a.id = cl.conID");


        sql.addField("a.id");
        sql.addField("a.name");
        sql.addField("a.address");
        sql.addField("a.city");
        sql.addField("a.countrySubdivision");
        sql.addField("a.zip");
        sql.addField("a.country");
        sql.setLimit(1000);

        sql.addWhere("a.type = 'Contractor'");
        sql.addWhere("cl.conID IS NULL");

        report.setLimit(1000);
    }
}
