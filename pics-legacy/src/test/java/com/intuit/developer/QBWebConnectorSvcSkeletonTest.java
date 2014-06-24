package com.intuit.developer;

import com.picsauditing.actions.converters.BooleanConverter;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;
import com.picsauditing.jpa.entities.Currency;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"QBWebConnectorSvcSkeletonTest-context.xml"})
public class QBWebConnectorSvcSkeletonTest {
    private QBWebConnectorSvcSkeleton qBWebConnectorSvcSkeleton;
	private Map<String, QBSession> sessions;
	private String TEST_PASSWORD = "test_password";
	
	@Mock private Authenticate authenticate;
	
	@Autowired private AppPropertyDAO appPropertyDAO;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		qBWebConnectorSvcSkeleton = new QBWebConnectorSvcSkeleton();
		sessions = Whitebox.getInternalState(QBWebConnectorSvcSkeleton.class, "sessions");
		
		when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADER);
		when(authenticate.getStrPassword()).thenReturn(TEST_PASSWORD);
	}
	
	@Test
	public void testAuthenticate_GENERAL_SETUP_NEEDS_REAL_USE_CASES() throws Exception {
		AppProperty maxSessions = new AppProperty();
		AppProperty sessionTimeout = new AppProperty();
		AppProperty qbPassword = new AppProperty();
		AppProperty shouldWeRunThisStep = new AppProperty();
		maxSessions.setValue("100");
		sessionTimeout.setValue("1000");
		qbPassword.setValue(TEST_PASSWORD);
		shouldWeRunThisStep.setValue("Y");
		
		when(appPropertyDAO.find("PICSQBLOADER.maxSessions")).thenReturn(maxSessions);
		when(appPropertyDAO.find("PICSQBLOADER.sessionTimeout")).thenReturn(sessionTimeout);
		when(appPropertyDAO.find("PICSQBLOADER.password")).thenReturn(qbPassword);
		when(appPropertyDAO.find(org.mockito.Mockito.startsWith("PICSQBLOADER.doStep."))).thenReturn(shouldWeRunThisStep);
		
		AuthenticateResponse response = qBWebConnectorSvcSkeleton.authenticate(authenticate);
		
		assertNotNull(response);
	}
	
	@Test
	public void testSetUpSession_PICSQBLOADER() throws Exception {
		when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADER);
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
		assertEquals(currencyCode, Currency.USD.name());
		String qbId = session.getQbID();
		assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_ID);
	}

	@Test
	public void testSetUpSession_PICSQBLOADERCAN() throws Exception {
		when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADERCAN);
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
		assertEquals(currencyCode, Currency.CAD.name());
		String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_CAID);
	}
	
	@Test
	public void testSetUpSession_PICSQBLOADERUK() throws Exception {
		when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADERUK);
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
        assertEquals(currencyCode, Currency.GBP.name());
		String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_UKID);
	}
	
	@Test
	public void testSetUpSession_PICSQBLOADEREU() throws Exception {
		when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADEREU);
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
        assertEquals(currencyCode, Currency.EUR.name());
		String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_EUID);
	}

    @Test
    public void testSetUpSession_PICSQBLOADERNOK() throws Exception {
        when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADERNOK);

        QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);

        assertCommonSessionFacts(session);
        String currencyCode = session.getCurrencyCode();
        assertEquals(currencyCode, Currency.EUR.name());
        String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_EUID);
    }

    @Test
    public void testSetUpSession_PICSQBLOADERSEK() throws Exception {
        when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADERSEK);

        QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);

        assertCommonSessionFacts(session);
        String currencyCode = session.getCurrencyCode();
        assertEquals(currencyCode, Currency.EUR.name());
        String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_EUID);
    }

    @Test
    public void testSetUpSession_PICSQBLOADERDKK() throws Exception {
        when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADERDKK);

        QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);

        assertCommonSessionFacts(session);
        String currencyCode = session.getCurrencyCode();
        assertEquals(currencyCode, Currency.EUR.name());
        String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_EUID);
    }

    @Test
    public void testSetUpSession_PICSQBLOADERCHF() throws Exception {
        when(authenticate.getStrUserName()).thenReturn(QBWebConnectorSvcSkeleton.PICSQBLOADERCHF);

        QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);

        assertCommonSessionFacts(session);
        String currencyCode = session.getCurrencyCode();
        assertEquals(currencyCode, Currency.CHF.name());
        String qbId = session.getQbID();
        assertEquals(qbId, QBWebConnectorSvcSkeleton.QB_LIST_CHID);
    }

    @Test
    public void testSetUpSession_PICSQBLOADEROTHERS() throws Exception {
        when(authenticate.getStrUserName()).thenReturn("PICSQBLOADEROTHERS");

        QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);

        assertCommonSessionFacts(session);
        String currencyCode = session.getCurrencyCode();
        assertNotSame(currencyCode, Currency.EUR.name());
        String qbId = session.getQbID();
        assertNotSame(qbId, QBWebConnectorSvcSkeleton.QB_LIST_EUID);
    }

	private void assertCommonSessionFacts(QBSession session) {
		String guid = session.getSessionId();
		assertThat(guid.length(), is(36));
		Date justNow = session.getLastRequest();
		assertThat(new Date().getTime() - justNow.getTime(), is(lessThan(3000L)));
		QBIntegrationWorkFlow currentStep = session.getCurrentStep();
		assertThat(currentStep, is(equalTo(QBIntegrationWorkFlow.DumpUnMappedContractors)));
	}
	
	@Ignore("Very long test")
	@Test
	public void testGuid_ReasonablyUnique() throws Exception {
		// this still passes at 100,000 but it is too slow
		int numToCreate = 10000;
		Set<String> guids = new HashSet<String>();
		for(int i = 0; i < numToCreate; i++) {
			String guid = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "guid");
			guids.add(guid);
		}
		
		// if any were duplicates, the unique set would disallow it and it would be less
		// than the number we tried to create
		assertThat(guids.size(), is(equalTo(numToCreate)));
	}

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADER() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADER);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERCAN() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERCAN);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERDKK() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERDKK);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADEREU() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADEREU);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERNOK() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERNOK);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERSEK() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERSEK);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERUK() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERUK);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERZAR() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERZAR);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADERCHF() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName(QBWebConnectorSvcSkeleton.PICSQBLOADERCHF);
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertTrue(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }

    @Test
    public void testIsKnownQBWCUsername_PICSQBLOADEROTHERS() throws Exception {
        Authenticate authenticate = new Authenticate();
        authenticate.setStrUserName("PICSQBLOADEROTHERS");
        Object isKnownQBWCUsername = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "isKnownQBWCUsername", authenticate);
        assertFalse(BooleanConverter.booleanValue(isKnownQBWCUsername));
    }


    }
