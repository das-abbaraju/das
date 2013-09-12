package com.intuit.developer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

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
		
		when(authenticate.getStrUserName()).thenReturn("PICSQBLOADER");
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
		when(authenticate.getStrUserName()).thenReturn("PICSQBLOADER");
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
		assertThat(currencyCode, is(equalTo("USD")));
		String qbId = session.getQbID();
		assertThat(qbId, is(equalTo("qbListID")));
	}

	@Test
	public void testSetUpSession_PICSQBLOADERCAN() throws Exception {
		when(authenticate.getStrUserName()).thenReturn("PICSQBLOADERCAN");
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
		assertThat(currencyCode, is(equalTo("CAD")));
		String qbId = session.getQbID();
		assertThat(qbId, is(equalTo("qbListCAID")));
	}
	
	@Test
	public void testSetUpSession_PICSQBLOADERUK() throws Exception {
		when(authenticate.getStrUserName()).thenReturn("PICSQBLOADERUK");
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
		assertThat(currencyCode, is(equalTo("GBP")));
		String qbId = session.getQbID();
		assertThat(qbId, is(equalTo("qbListUKID")));
	}
	
	@Test
	public void testSetUpSession_PICSQBLOADEREU() throws Exception {
		when(authenticate.getStrUserName()).thenReturn("PICSQBLOADEREU");
		
		QBSession session = Whitebox.invokeMethod(qBWebConnectorSvcSkeleton, "setUpSession", authenticate);
		
		assertCommonSessionFacts(session);
		String currencyCode = session.getCurrencyCode();
		assertThat(currencyCode, is(equalTo("EUR")));
		String qbId = session.getQbID();
		assertThat(qbId, is(equalTo("qbListEUID")));
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
}
