package com.picsauditing.service.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.picsauditing.jpa.entities.TranslationQualityRating;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.sun.jersey.api.client.*;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class TranslateRestClientTest {
    private static final String TEST_KEY = "Test.Key";
    private static final String TEST_TRANSLATION = "Testing testing... is this thing on?";
    private static final ObjectMapper mapper = new ObjectMapper();

    private TranslateRestClient translateRestClient;

    @Mock
    private WebResource webResource;
    @Mock
    private ClientResponse response;
    @Mock
    private WebResource.Builder builder;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    protected HttpServletRequest request;
    @Mock
    private Client client;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translateRestClient = new TranslateRestClient();

        when(client.resource(anyString())).thenReturn(webResource);
        when(webResource.accept(MediaType.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(webResource.path(anyString())).thenReturn(webResource);
        when(builder.get(ClientResponse.class)).thenReturn(response);
        when(response.getStatus()).thenReturn(200);

        TranslateRestClient.registerWebClient(client);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        TranslateRestClient.resetWebClient();
    }

    @Test
    public void testTranslationFromWebResource_Happy() throws Exception {
        TranslationResponse responseO = new TranslationResponse(TEST_KEY, TEST_TRANSLATION, "en", TranslationQualityRating.Good);
        when(response.getEntity(String.class)).thenReturn(makeJson(responseO, responseO));

        TranslationWrapper translation = translateRestClient.translationFromWebResource(TEST_KEY, "en");

        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TEST_TRANSLATION.equals(translation.getTranslation()));
        assertTrue("en".equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Good.equals(translation.getQualityRating()));
    }

    @Test
    public void testTranslationFromWebResource_CreatesTranslationFromBest() throws Exception {
        TranslationResponse best = new TranslationResponse(TEST_KEY, TEST_TRANSLATION, "en", TranslationQualityRating.Good);
        TranslationResponse requested = new TranslationResponse(TEST_KEY, "French " + TEST_TRANSLATION, "fr", TranslationQualityRating.Bad);
        when(response.getEntity(String.class)).thenReturn(makeJson(best, requested));

        TranslationWrapper translation = translateRestClient.translationFromWebResource(TEST_KEY, "en");

        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TEST_TRANSLATION.equals(translation.getTranslation()));
        assertTrue("en".equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Good.equals(translation.getQualityRating()));
    }

    @Test
    public void testTranslationFromWebResource_BadResponseGivesErrorTranslation() throws Exception {
        when(client.resource(anyString())).thenReturn(webResource);
        when(response.getStatus()).thenReturn(500);

        TranslationWrapper translation = translateRestClient.translationFromWebResource(TEST_KEY, "en");

        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TranslationService.ERROR_STRING.equals(translation.getTranslation()));
        assertTrue("en".equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Bad.equals(translation.getQualityRating()));
    }

    @Test
    public void testTranslationsFromWebResourceByWildcard_BadResponseGivesErrorTranslation() throws Exception {
        when(response.getStatus()).thenReturn(500);

        List<TranslationWrapper> translations = translateRestClient.translationsFromWebResourceByWildcard(TEST_KEY, "en");
        TranslationWrapper translation = translations.get(0);

        assertTrue(TEST_KEY.equals(translation.getKey()));
        assertTrue(TranslationService.ERROR_STRING.equals(translation.getTranslation()));
        assertTrue("en".equals(translation.getLocale()));
        assertTrue(TranslationQualityRating.Bad.equals(translation.getQualityRating()));
    }

    @Test
    public void testTranslationsFromWebResourceByWildcard_Happy() throws Exception {
        TranslationResponse responseO = new TranslationResponse(TEST_KEY, TEST_TRANSLATION, "en", TranslationQualityRating.Good);
        List<TranslationResponse> responses = new ArrayList();
        responses.add(responseO);
        responses.add(responseO);
        when(response.getEntity(String.class)).thenReturn(makeJson(responses));

        List<TranslationWrapper> translations = translateRestClient.translationsFromWebResourceByWildcard(TEST_KEY, "en");

        for (TranslationWrapper translation : translations) {
            assertTrue(TEST_KEY.equals(translation.getKey()));
            assertTrue(TEST_TRANSLATION.equals(translation.getTranslation()));
            assertTrue("en".equals(translation.getLocale()));
        }
    }

    private String makeJson(List stuff) throws JsonProcessingException {
        return mapper.writeValueAsString(stuff);
    }

    private String makeJson(TranslationResponse best, TranslationResponse requested) throws JsonProcessingException {
        return mapper.writeValueAsString(new TranslationResponses(best, requested));
    }

    private class TranslationResponses {
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

    private class TranslationResponse {
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
