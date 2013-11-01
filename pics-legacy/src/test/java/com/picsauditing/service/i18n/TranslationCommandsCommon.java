package com.picsauditing.service.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class TranslationCommandsCommon {
    private static final ObjectMapper mapper = new ObjectMapper();

    static final String TEST_KEY = "Test.Key";
    static final String TEST_LOCALE = "en";
    static final String TEST_TRANSLATION = "Testing testing... is this thing on?";

    @Mock
    WebResource webResource;
    @Mock
    ClientResponse response;
    @Mock
    Client client;
    @Mock
    WebResource.Builder builder;

    @Before
    public void setUpCommon() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(client.resource(anyString())).thenReturn(webResource);
        when(webResource.accept(MediaType.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(webResource.path(anyString())).thenReturn(webResource);
        when(builder.get(ClientResponse.class)).thenReturn(response);
        when(response.getStatus()).thenReturn(200);

        TranslateCommand.registerWebClient(client);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        TranslateCommand.resetWebClient();
    }

    String makeJson(List stuff) throws JsonProcessingException {
        return mapper.writeValueAsString(stuff);
    }

    String makeJson(TranslationResponse best, TranslationResponse requested) throws JsonProcessingException {
        return mapper.writeValueAsString(new TranslationResponses(best, requested));
    }

    void verifyErrorTranslation(TranslationWrapper translation) {
        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TranslationService.ERROR_STRING.equals(translation.getTranslation()));
        assertTrue(TEST_LOCALE.equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Bad.equals(translation.getQualityRating()));
    }


    class TranslationResponses {
        private TranslationResponse best;
        private TranslationResponse requested;

        public TranslationResponses(TranslationResponse best, TranslationResponse requested) {
            this.best = best;
            this.requested = requested;
        }

        public TranslationResponse getBest() {
            return best;
        }

        public void setBest(TranslationResponse best) {
            this.best = best;
        }

        public TranslationResponse getRequested() {
            return requested;
        }

        public void setRequested(TranslationResponse requested) {
            this.requested = requested;
        }
    }

    class TranslationResponse {
        private String key;
        private String value;
        private String locale;
        private TranslationQualityRating qualityRating;

        public TranslationResponse() {}

        public TranslationResponse(String key, String value, String locale, TranslationQualityRating qualityRating) {
            this.key = key;
            this.value = value;
            this.locale = locale;
            this.qualityRating = qualityRating;

        }
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public TranslationQualityRating getQualityRating() {
            return qualityRating;
        }

        public void setQualityRating(TranslationQualityRating qualityRating) {
            this.qualityRating = qualityRating;
        }
    }
}
