package com.picsauditing.service.i18n;

import com.google.common.collect.Table;
import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.model.i18n.TranslationWrapper;
import com.picsauditing.util.SpringUtils;
import com.sun.jersey.api.client.*;
import net.sf.ehcache.*;
import org.apache.struts2.StrutsStatics;
import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class TranslationServiceAdapterTest {
    private static final String TEST_KEY = "TestKey";
    private static final String TEST_TRANSLATION = "This is a test string";
    private static final Locale TEST_LOCALE = Locale.ENGLISH;
    private static final Locale TEST_LOCALE_OTHER = Locale.FRENCH;
    private static final String TEST_RESPONSE = "{\"key\":\"Test\",\"value\":\"This is a test string\",\"locale\":\"en\"}";
    private static final String TEST_RESPONSE_OTHER = "{\"key\":\"Test\",\"value\":\"Viola une phrase\",\"locale\":\"fr\"}";

    private TranslationServiceAdapter translationService;
    private Cache cache;
    private Cache wildcardCache;
    private ActionContext actionContext;
    private Map<String, Object> context;

    @Mock
    private Client client;
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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        translationService = TranslationServiceAdapter.getInstance();
        Whitebox.setInternalState(translationService, "client", client);
        Whitebox.setInternalState(SpringUtils.class, "applicationContext", applicationContext);
        cache = Whitebox.getInternalState(TranslationServiceAdapter.class, "cache");
        wildcardCache = Whitebox.getInternalState(TranslationServiceAdapter.class, "wildcardCache");

        when(client.resource(anyString())).thenReturn(webResource);
        when(webResource.accept(MediaType.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(builder.get(ClientResponse.class)).thenReturn(response);
        when(response.getStatus()).thenReturn(200);
        when(response.getEntity(String.class)).thenReturn(TEST_RESPONSE);

        context = new HashMap<>();
        context.put(StrutsStatics.HTTP_REQUEST, request);
        actionContext = new ActionContext(context);
        ActionContext.setContext(actionContext);

    }

    @After
    public void tearDown() throws Exception {
        Whitebox.setInternalState(translationService, "client", (Client)null);
        cache.remove(TEST_KEY);
    }

//    @AfterClass
//    public static void classTearDown() throws Exception {
//    }

    @Test
    public void testNullActionNameParsesFromReferrer() throws Exception {
        context.put(ActionContext.ACTION_NAME, null);
        StringBuffer referrer = new StringBuffer("http://localhost:9000/api/en_US/ContractorRegistrationServices.title?referrer=http://ajiva.local:8080/RegistrationServiceEvaluation.action");
        when(request.getRequestURL()).thenReturn(referrer);

        String pageName = Whitebox.invokeMethod(translationService, "pageName");

        assertTrue("RegistrationServiceEvaluation".equals(pageName));

    }

    @Test
    public void testParsePageFromReferrer() throws Exception {
        StringBuffer referrer = new StringBuffer("http://localhost:9000/api/en_US/ContractorRegistrationServices.title?referrer=http://ajiva.local:8080/RegistrationServiceEvaluation.action");
        when(request.getRequestURL()).thenReturn(referrer);

        String pageName = Whitebox.invokeMethod(translationService, "parsePageFromReferrer");

        assertTrue("RegistrationServiceEvaluation".equals(pageName));
    }

    @Test
    public void testGetText_NotCachedResultsInServiceCall() throws Exception {

        translationService.getText(TEST_KEY, TEST_LOCALE.getDisplayLanguage());

        verify(client).resource(anyString());
        verify(builder).get(ClientResponse.class);
    }

    @Test
    public void testGetText_NotCachedResultsInOneServiceCallForMultipleCalls() throws Exception {
        translationService.getText(TEST_KEY, TEST_LOCALE.getDisplayLanguage());
        translationService.getText(TEST_KEY, TEST_LOCALE.getDisplayLanguage());

        verify(client, times(1)).resource(anyString());
        verify(builder, times(1)).get(ClientResponse.class);
    }

    @Test
    public void testGetText_CachedDoesNotResultInServiceCall() throws Exception {
        cacheTestTranslation();

        translationService.getText(TEST_KEY, TEST_LOCALE.getDisplayLanguage());

        verify(client, never()).resource(anyString());
        verify(builder, never()).get(ClientResponse.class);
    }

    @Test
    public void testGetText_CachedKeyButLocaleCacheMissResultsInServiceCall() throws Exception {
        cacheTestTranslation();
        when(response.getEntity(String.class)).thenReturn(TEST_RESPONSE_OTHER);

        String translation = translationService.getText(TEST_KEY, TEST_LOCALE_OTHER.getDisplayLanguage());

        verify(client).resource(anyString());
        verify(builder).get(ClientResponse.class);
    }

    @Test
    public void testGetText_CachedKeyButLocaleCacheMissResultsInCaching() throws Exception {
        cacheTestTranslation();
        when(response.getEntity(String.class)).thenReturn(TEST_RESPONSE_OTHER);

        // the first will miss the cache and cache the result
        translationService.getText(TEST_KEY, TEST_LOCALE_OTHER.getDisplayLanguage());
        // if the result is properly cached, this call will pull from cache
        translationService.getText(TEST_KEY, TEST_LOCALE_OTHER.getDisplayLanguage());

        // resulting in only one service call
        verify(client, times(1)).resource(anyString());
        verify(builder, times(1)).get(ClientResponse.class);
    }

    private void cacheTestTranslation() throws Exception {
        TranslationWrapper translation = new TranslationWrapper.Builder().translation(TEST_TRANSLATION).build();
        Whitebox.invokeMethod(translationService, "cacheTranslationIfReturned", new Object[] { TEST_KEY, TEST_LOCALE.getDisplayLanguage(), translation});
    }

    @Test
    public void testWildCardCaching() throws Exception {
        List<TranslationWrapper> translations = englishTranslations();

        Whitebox.invokeMethod(translationService, "cacheWildcardTranslation", new Object[] {"Test.WildCard", "en", translations });

        List<TranslationWrapper> cachedTranslations = Whitebox.invokeMethod(translationService, "translationsFromWildCardCache", "Test.WildCard", "en");
        assertSame(translations, cachedTranslations);
    }

    @Test
    public void testWildCardCaching_MixedLocales() throws Exception {
        List<TranslationWrapper> enTranslations = englishTranslations();
        List<TranslationWrapper> frTranslations = frenchTranslations();

        Whitebox.invokeMethod(translationService, "cacheWildcardTranslation", new Object[] {"Test.WildCard", "en", enTranslations });
        Whitebox.invokeMethod(translationService, "cacheWildcardTranslation", new Object[] {"Test.WildCard", "fr", frTranslations });

        List<TranslationWrapper> cachedTranslations = Whitebox.invokeMethod(translationService, "translationsFromWildCardCache", "Test.WildCard", "en");
        assertSame(enTranslations, cachedTranslations);
    }

    private List<TranslationWrapper> englishTranslations() {
        List<TranslationWrapper> translations = new ArrayList<>();
        translations.add(new TranslationWrapper.Builder().key(TEST_KEY + "1").locale("en").translation("One").build());
        translations.add(new TranslationWrapper.Builder().key(TEST_KEY + "2").locale("en").translation("Two").build());
        translations.add(new TranslationWrapper.Builder().key(TEST_KEY + "3").locale("en").translation("Three").build());
        return translations;
    }

    private List<TranslationWrapper> frenchTranslations() {
        List<TranslationWrapper> frTranslations = new ArrayList<>();
        frTranslations.add(new TranslationWrapper.Builder().key(TEST_KEY + "1").locale("fr").translation("Un").build());
        frTranslations.add(new TranslationWrapper.Builder().key(TEST_KEY + "2").locale("fr").translation("Deux").build());
        frTranslations.add(new TranslationWrapper.Builder().key(TEST_KEY + "3").locale("fr").translation("Trois").build());
        return frTranslations;
    }

    private void assertSame(List<TranslationWrapper> translations, List<TranslationWrapper> cachedTranslations) {
        int found = 0;
        for (TranslationWrapper translation : translations) {
            for (TranslationWrapper cachedTranslation : cachedTranslations) {
                if (translation.getKey() == cachedTranslation.getKey() &&
                    translation.getTranslation() == cachedTranslation.getTranslation() &&
                    translation.getLocale() == cachedTranslation.getLocale()) {
                    found++;
                }
            }
        }
        assertTrue(found == translations.size());
    }
}
