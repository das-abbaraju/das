package com.picsauditing.validator;

import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.util.EmailAddressUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VATWebValidator {
    private final Logger logger = LoggerFactory.getLogger(VATWebValidator.class);

    public void webValidate(String vatCode) throws ValidationException {
        String countryPrefix = vatCode.substring(0,2);
        String numbers = vatCode.substring(2, vatCode.length());

        String result = null;
        try {
            result = runValidation(countryPrefix, numbers);
        } catch (IOException e) {
            logger.error("Trouble connecting to http://isvat.appspot.com/ using VAT Code: "
                    + vatCode
                    + ".  This is potentially business critical!");
        }

        if (!result.equals("true")) {
            throw new ValidationException();
        }
    }


    private String runValidation(String countryPrefix, String numbers) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(
                "http://isvat.appspot.com/" + countryPrefix + "/" + numbers +"/"
        ).openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        OutputStream output = connection.getOutputStream();
        output.close();

        InputStream input = connection.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        String lin = br.readLine();

        return lin;
    }
}
