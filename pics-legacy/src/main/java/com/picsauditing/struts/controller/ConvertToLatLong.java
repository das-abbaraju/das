package com.picsauditing.struts.controller;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.ContractorLocationDao;
import com.picsauditing.persistence.model.ContractorLocation;
import com.picsauditing.search.SelectSQL;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.*;

public class ConvertToLatLong extends ReportActionSupport {

    public static final int LIMIT = 10;
    @Autowired
    private ContractorLocationDao contractorLocationDao;

    public static final JSONParser JSON_PARSER = new JSONParser();

    private Stack<Pair<String, String>> googleKeys = new Stack<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public enum GoogleCallResult {
        OK("OK"),
        ZERO_RESULTS("ZERO_RESULTS"),
        OVER_QUERY_LIMIT("OVER_QUERY_LIMIT"),
        REQUEST_DENIED("REQUEST_DENIED"),
        INVALID_REQUEST("INVALID_REQUEST"),
        UNKNOWN_ERROR("UNKNOWN_ERROR"),
        UNDEFINED("UNDEFINED");//internal (non-google)

        private String value = "OK";

        private GoogleCallResult(String v) {
            this.value = v;
        }

        String getValue() {
            return value;
        }

        public static GoogleCallResult returMatching(String status) {
            if (status != null) {
                if (OK.getValue().equals(status)) return OK;
                if (ZERO_RESULTS.getValue().equals(status)) return ZERO_RESULTS;
                if (OVER_QUERY_LIMIT.getValue().equals(status)) return OVER_QUERY_LIMIT;
                if (REQUEST_DENIED.getValue().equals(status)) return REQUEST_DENIED;
                if (INVALID_REQUEST.getValue().equals(status)) return INVALID_REQUEST;
                if (UNKNOWN_ERROR.getValue().equals(status)) return UNKNOWN_ERROR;
            }
            return null;
        }
    }

    {
        googleKeys.push(new Pair<>("nkishore@picsauditing.com", "AIzaSyDJbNqeYee7A_06jBb5_3Y8g4yVW3yu9l8"));
        googleKeys.push(new Pair<>("dabbaraju@picsauditing.com", "AIzaSyA6_NbYqni-Oqn5LU8JEUFXkK_LG5e3gz4"));

        googleKeys.push(new Pair<>("dabbaraju@picsauditing.com", "AIzaSyDbGEeHbtm20hb5hJC8WUYeE4Vl7ET2JfQ"));
        googleKeys.push(new Pair<>("dalvarado@picsauditing.com", "AIzaSyDOYSXCvy7vDkHftdqHQbOpOpZ1uqlEi3A"));
        googleKeys.push(new Pair<>("ssalaheen@picsauditing.com", "AIzaSyDymIsBVP0-YIKLuNaZnP2uRrrowM4ZWgY"));
        googleKeys.push(new Pair<>("pducuron@picsauditing.com", "AIzaSyCAg21hX8Ex2u5dOX5JY70sQvCSGL2KuY4"));
        googleKeys.push(new Pair<>("gergs@picsauditing.com", "AIzaSyDfC7CZKRb0BBZjrq8x2X7-7OikkUWYyto"));
    }


    public String execute() throws SQLException, ParseException, IOException {
        convertAddressToLatLong();
        return BLANK;
    }

    private void convertAddressToLatLong() throws SQLException, IOException, ParseException {

        SelectSQL sql = new SelectSQL("accounts a");
        buildQuery(sql);

        run(sql);

        debug("processing " + getData().size() + " rows...");

        Pair<Boolean, Set<ContractorLocation>> result = processData();

        Set<ContractorLocation> contractorLocationSet = result.getRight();

        if (!contractorLocationSet.isEmpty()) {
            persistToDatabase(contractorLocationSet);
        }

        debug("finished processing.");


    }

    private Pair<Boolean, Set<ContractorLocation>> processData() throws ParseException, IOException {
        Boolean allGood;
        Set<ContractorLocation> contractorLocationSet = new HashSet<>();


        for (BasicDynaBean datum : getData()) {

            int conId = (Integer) datum.get("id");
            String address = parseAddress(datum);

            debug("processing conId: " + conId);

            if (valid(address)) {

                Pair<Boolean, ContractorLocation> result = getLatLong(address, conId, getCurrentGoogleApiKey());

                allGood = result.getLeft();

                if (!allGood) {
                    return new Pair<>(allGood, contractorLocationSet);
                }

                ContractorLocation contractorLocation = result.getRight();

                if (notNull(contractorLocation))
                    contractorLocationSet.add(contractorLocation);
                else
                    debug("Could not find contractor location for address: " + address + " id: " + conId);
            }
        }
        return new Pair<>(true, contractorLocationSet);
    }

    private Pair<Boolean, ContractorLocation> getLatLong(String address, int conId, String googleApiKey) {
        Boolean allGood = Boolean.TRUE;
        ContractorLocation contractorLocation;

        GoogleCallResult googleCallResult;

        try {
            Pair<GoogleCallResult, String> result = sendRequestToGoogle(address, googleApiKey);
            googleCallResult = result.getLeft();

            if (googleCallFailed(googleCallResult)) {

                if (GoogleCallResult.REQUEST_DENIED.equals(googleCallResult)) {

                    debug("Rejected APi key: " + googleApiKey + " trying next available key");

                    if (hasMoreKeysToTry()) {
                        return getLatLong(address, conId, getNextGoogleApiKey());
                    } else {
                        return new Pair<>(Boolean.FALSE, null);
                    }
                } else {
                    logger.error("call to Google Geo API failed, error: {0}", googleCallResult.getValue());
                }

            } else {
                JSONObject location = findLocationObject(result.getRight());

                if (location == null) {
                    logger.error("could not find location for address: {0} contractorId: {1}", address, conId);
                } else {
                    Double lng = (Double) location.get("lng");
                    Double lat = (Double) location.get("lat");

                    if (notNull(lng) && notNull(lat)) {

                        logger.debug("Address: {0}", address);

                        contractorLocation = ContractorLocation.createFrom(conId, lat.floatValue(), lng.floatValue(), 38586);
                        return new Pair<>(allGood, contractorLocation);
                    }
                }
            }

        } catch (IOException | ParseException pe) {
            logger.error("Error getting latitud and longitud for address {0} contractorId {0}", address, conId);
        }

        return null;
    }

    private void persistToDatabase(Set<ContractorLocation> contractorLocations) {
        for (ContractorLocation contractorLocation : contractorLocations) {
            contractorLocationDao.insertContractorLocation(contractorLocation);
        }
    }

    private boolean googleCallIsOk(String body) throws ParseException {
        return getGoogleCallStatusResult(body) == GoogleCallResult.OK;
    }

    private GoogleCallResult getGoogleCallStatusResult(String body) {
        try {
            JSONObject object = (JSONObject) JSON_PARSER.parse(body);

            return GoogleCallResult.returMatching(object.get("status").toString());

        } catch (Exception e) {
            return GoogleCallResult.UNDEFINED;
        }
    }

    private JSONObject findLocationObject(String body) throws ParseException {
        JSONObject geometry;
        JSONObject object = (JSONObject) JSON_PARSER.parse(body);

        JSONArray results = (JSONArray) object.get("results");
        JSONObject firstResult = (JSONObject) results.get(0);
        geometry = (JSONObject) firstResult.get("geometry");
        return (JSONObject) geometry.get("location");
    }

    private Pair<GoogleCallResult, String> sendRequestToGoogle(String address, final String apiKey) throws IOException {
        String body;
        URL url = new URL(
                "https://maps.googleapis.com/maps/api/geocode/json?address="
                        + address
                        + "&sensor=false&key=" + apiKey
        );
        URLConnection con = url.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();
        encoding = encoding == null ? "UTF-8" : encoding;
        body = IOUtils.toString(in, encoding);

        return new Pair<>(getGoogleCallStatusResult(body), body);
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
        sql.setLimit(LIMIT);

        sql.addWhere("a.type = 'Contractor'");
        sql.addWhere("cl.conID IS NULL");

        report.setLimit(LIMIT);
    }

    private boolean googleCallFailed(GoogleCallResult googleCallResult) {
        return !googleCallResult.equals(GoogleCallResult.OK);
    }

    private String getCurrentGoogleApiKey() {
        return googleKeys.peek().getRight();
    }

    private String getCurrentKeyOwner() {
        return googleKeys.peek().getLeft();
    }

    private boolean hasMoreKeysToTry() {
        return !googleKeys.isEmpty();
    }

    private String getNextGoogleApiKey() {
        Pair<String, String> p = googleKeys.pop();
        debug("next key: " + p.getRight() + " (" + p.getLeft() + ")");
        return p.getRight();
    }

    private boolean notNull(Object v) {
        return v != null;
    }

    private boolean valid(String v) {
        return v != null && !v.isEmpty();
    }

    private void debug(String s) {
        System.out.println(s);
    }

    class Pair<LEFT, RIGHT> {
        LEFT left;
        RIGHT right;

        public Pair(LEFT left, RIGHT right) {
            this.left = left;
            this.right = right;
        }

        LEFT getLeft() {
            return left;
        }

        RIGHT getRight() {
            return right;
        }
    }
}
