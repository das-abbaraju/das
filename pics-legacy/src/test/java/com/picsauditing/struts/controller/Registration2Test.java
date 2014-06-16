package com.picsauditing.struts.controller;

import com.picsauditing.PicsActionTest;
import org.junit.Test;

import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class Registration2Test extends PicsActionTest {

    @Test
    public void testGetKeysFromJson() throws Exception {

        String json = "{\n" +
                "        \"countryISOCode\": \"US\",\n" +
                "        \"timezone\": {\"ID\":\"America/Los_Angeles\"},\n" +
                "        \"legalName\": \"My Company1396653571606\",\n" +
                "        \"addressBlob\": \"4038 Germainder Way Irvine, CA\",\n" +
                "        \"zip\": \"92612\",\n" +
                "        \"firstName\": \"John\",\n" +
                "        \"lastName\": \"Doe\",\n" +
                "        \"email\": \"my.email1396653740071@test.com\",\n" +
                "        \"phone\": \"555-555-5555\",\n" +
                "        \"username\": \"my.email1396653740071@test.com\",\n" +
                "        \"password\": \"password1\",\n" +
                "        \"passwordConfirmation\": \"password1\"\n" +
                "    }";

        Registration2 registration2 = new Registration2();
        Set<String> keysFromJson = registration2.getKeysFromJson(json, "registrationForm");

        assertEquals("[registrationForm.addressBlob, registrationForm.countryISOCode, registrationForm.email, registrationForm.firstName, registrationForm.lastName, registrationForm.legalName, registrationForm.password, registrationForm.passwordConfirmation, registrationForm.phone, registrationForm.timezone, registrationForm.username, registrationForm.zip]", keysFromJson.toString());

    }
}
