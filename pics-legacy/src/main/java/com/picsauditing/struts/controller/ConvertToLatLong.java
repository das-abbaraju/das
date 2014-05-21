package com.picsauditing.struts.controller;

import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.companyfinder.model.ContractorLocation;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.companyfinder.ContractorLocationDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.search.SelectSQL;
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
import java.util.List;

public class ConvertToLatLong extends ReportActionSupport {
    @Autowired
    private ContractorLocationDAO contractorLocationDAO;

    @Autowired
    private ContractorAccountDAO contractorAccountDAO;

    public static final JSONParser JSON_PARSER = new JSONParser();
    private SelectSQL sql;

    public String execute() throws SQLException, ParseException, IOException {
//        sql = new SelectSQL("accounts a");
//        buildQuery(sql);
//
//        run(sql);
        List<ContractorAccount> contractors = contractorAccountDAO.findContractorsLatLong();

        for (ContractorAccount contractor : contractors) {
            Double lng = new Double(0);
            Double lat = new Double(0);

            String address = null;
            int conId = 0;
            try {
                conId = contractor.getId();
                address = parseAddress(contractor);
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
                        try {
                            lng = (Double) location.get("lng");
                            lat = (Double) location.get("lat");

                            if (lng != null && lat != null) {
                                System.out.println(address);
                                System.out.println(contractors.indexOf(contractor));
                            }
                        } catch (Exception e) {

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

        ContractorLocation contractorLocation = ContractorLocation.builder()
                .contractor(ContractorAccount.builder()
                        .id(conId)
                        .build())
                .lat(lat)
                .lng(lng)
                .build();
        contractorLocation.setAuditColumns(user);
        contractorLocationDAO.save(contractorLocation);
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

    // lkam pics: AIzaSyD8q24WBB7v0PRaCG0gAaV0zq7jsXvYmJI
    // lkam88: AIzaSyA6_NbYqni-Oqn5LU8JEUFXkK_LG5e3gz4
    // Phil S: pics AIzaSyADXeKAoBKj2BXQ5NqhYAno8c1oP7qCfLA
    // Siraj: AIzaSyAg8eJXOTvr3SkLhnHTC-AjOHmJ9ueD9x8
    // David: AIzaSyBfMHt5U2x8WjOatB6k8vvLU3NxV0K3BJI
    // Gergs: AIzaSyDfC7CZKRb0BBZjrq8x2X7-7OikkUWYyto
    // Das: AIzaSyAOBTg8F-p7zH-734JFV0rdszo1ZTPQAwk
    private String sendRequestToTheGoogles(String address) throws IOException {
        String body;
        URL url = new URL(
                "https://maps.googleapis.com/maps/api/geocode/json?address="
                        + address
                        + "&sensor=false&key=AIzaSyAOBTg8F-p7zH-734JFV0rdszo1ZTPQAwk"
        );
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        body = IOUtils.toString(in, encoding);
        return body;
    }

    private String parseAddress(ContractorAccount contractor) {
        String address;
        address =
                (contractor.getAddress()).replace(" ", "+") +
                        "+" + (contractor.getCity()).replace(" ", "+") +
                        "+" + (contractor.getCountrySubdivision().getIsoCode()).replace("US-", "") +
                        "+" + contractor.getZip() +
                        "+" + contractor.getCountry().getIsoCode();
        return address;
    }

    private void buildQuery(SelectSQL sql) {
        sql.addJoin("LEFT JOIN contractor_location cl ON a.id = cl.conID");


        sql.addField("a.id");
        sql.addField("a.name");
        sql.addField("a.address");
        sql.addField("a.city");
        sql.addField("a.countrySubdivision");
        sql.addField("a.zip");
        sql.addField("a.country");
        sql.setLimit(1000);

        sql.addWhere("a.type = 'Contractor'");
        sql.addWhere("cl.id IS NULL");

        report.setLimit(1000);
    }
}
