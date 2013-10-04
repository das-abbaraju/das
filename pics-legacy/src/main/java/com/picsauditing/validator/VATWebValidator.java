package com.picsauditing.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VATWebValidator {
    private static final Logger logger = LoggerFactory.getLogger(VATWebValidator.class);

    public void webValidate(String vatCode) throws ValidationException {
        String countryPrefix = vatCode.substring(0, 2);
        String numbers = vatCode.substring(2, vatCode.length());

        String result = null;
        try {
            result = runValidation(countryPrefix, numbers);
        } catch (IOException e) {
            logger.error("Trouble connecting to http://isvat.appspot.com/ using VAT Code: "
                    + vatCode
                    + ".  This is potentially business critical!");
        }

        if (!"true".equals(result)) {
            throw new ValidationException();
        }
    }

    private String runValidation(String countryPrefix, String numbers) throws IOException {
        HttpURLConnection connection = null;
        OutputStream output = null;
        BufferedReader br = null;
        String result = null;
        try {
            connection = (HttpURLConnection) new URL(
                    "http://isvat.appspot.com/" + countryPrefix + "/" + numbers + "/"
            ).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            output = connection.getOutputStream();

            InputStream input = connection.getInputStream();

            br = new BufferedReader(new InputStreamReader(input));
            result = br.readLine();
        } catch (IOException e) {
            // Don't keep the contractor from registering if there's a connection problem.
            result = "true" ;
            throw e;
        }
        finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {}
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {}
            }

           return result;
        }
    }
}
