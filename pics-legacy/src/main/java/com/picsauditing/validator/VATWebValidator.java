package com.picsauditing.validator;

import com.netflix.hystrix.*;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/*
    Please see https://github.com/Netflix/Hystrix/wiki
 */
public class VATWebValidator extends HystrixCommand<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(VATWebValidator.class);
    private static final String VALIDATION_URL = "http://isvat.appspot.com";
    private static Client client;
    private static WebResource webResource;
    private static final String HYSTRIX_COMMAND_GROUP = "VATWebValidator";
    private static final String VALIDATION_OK_STRING = "true";
    private static final String ERROR_STRING = "Trouble validating VAT code using http://isvat.appspot.com/. VAT code was: {}.";
    static final int THREAD_TIMEOUT_MS = 5000;
    private static final int THREAD_POOL_SIZE = 20;
    private static final int WEB_CONNECT_TIMEOUT_MS = 1000;
    private static final int WEB_READ_TIMEOUT_MS = 1000;
    private String vatCode;

    /*
        The jersey client is threadsafe as long as you don't attempt to change the configuration after creation.
        Also, using getEntity (get(String)), it will close its own connections/resources/streams.
     */
    static {
        ClientConfig cc = new DefaultClientConfig();
        Map<String, Object> props = cc.getProperties();
        props.put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, WEB_CONNECT_TIMEOUT_MS);
        props.put(ClientConfig.PROPERTY_READ_TIMEOUT, WEB_READ_TIMEOUT_MS);
        client = Client.create(cc);
        webResource = client.resource(VALIDATION_URL);
    }

    public VATWebValidator(String vatCode) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(HYSTRIX_COMMAND_GROUP))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter().withExecutionIsolationThreadTimeoutInMilliseconds(THREAD_TIMEOUT_MS)
            ).andThreadPoolPropertiesDefaults(
                HystrixThreadPoolProperties.Setter().withCoreSize(THREAD_POOL_SIZE)
            )
        );
        this.vatCode = vatCode;
    }

    @Override
    protected Boolean run() throws Exception {
        return webValidate();
    }

    @Override
    protected Boolean getFallback() {
        logger.debug(ERROR_STRING, vatCode);
        return Boolean.TRUE;
    }

    public boolean webValidate() throws Exception {
        String countryPrefix;
        String numbers;
        try {
            countryPrefix = vatCode.substring(0, 2);
            numbers = vatCode.substring(2, vatCode.length());
        } catch (Exception e) {
            return false;
        }
        return runValidation(countryPrefix, numbers);
    }

    private boolean runValidation(String countryPrefix, String numbers) throws Exception {
        String response = webResource.path(countryPrefix + "/" + numbers).get(String.class);
        if (VALIDATION_OK_STRING.equals(response)) {
            return true;
        } else {
            return false;
        }
    }

    // for injecting test client for unit tests
    public static void registerWebClient(Client webclient) {
        client = webclient;
        webResource = client.resource(VALIDATION_URL);
    }
}
