package com.picsauditing.dao;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.search.Database;
import com.picsauditing.util.EmailAddressUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(EmailAddressUtils.class)
public class EmailQueueDAOTest {
	private EmailQueueDAO emailQueueDAO;
	@Mock
	private EmailAddressUtils emailAddressUtils;
	@Mock
	private Database database;
	@Mock
	private PicsDAO picsDAO;
	@Mock
	private EntityManager em;
	@Mock
	private QueryMetaData queryMetaData;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		emailQueueDAO = new EmailQueueDAO();
		Whitebox.setInternalState(emailQueueDAO, "database", database);
		Whitebox.setInternalState(emailQueueDAO, "em", em);
		Whitebox.setInternalState(emailQueueDAO, "queryMetaData", queryMetaData);
		PowerMockito.mockStatic(EmailAddressUtils.class);
	}

	@Test
	public void testAddEmailAddressExclusions_invalidEmail() throws Exception {
		when(EmailAddressUtils.isValidEmail((String) any())).thenReturn(false);
		// verify(database).executeUpdate((String) any());
	}

	@Test
	public void testAddEmailAddressExclusions_validEmail() throws Exception {
		when(EmailAddressUtils.isValidEmail((String) any())).thenReturn(true);
		// verify(database).executeInsert((String) any());
	}

	@Test
	public void testAddEmailAddressExclusions_activeUser() throws Exception {
		//when(Whitebox.invokeMethod(emailQueueDAO, "findActiveUserEmail", (String) any())).thenReturn(true);
		// verify(database).executeInsert((String) any());
	}

	@Test
	public void testAddEmailAddressExclusions_inActiveUser() throws Exception {
		//when(Whitebox.invokeMethod(emailQueueDAO, "findActiveUserEmail", (String) any())).thenReturn(false);
		// verify(database).executeInsert((String) any());
	}

	@Test
	public void testAddEmailAddressExclusions_inExclusionList() throws Exception {
		//when(emailQueueDAO.findEmailAddressExclusionAlreadyExists((String) any())).thenReturn(true);
		// verify(database).executeInsert((String) any());
	}

	@Test
	public void testAddEmailAddressExclusions_NotInExclusionList() throws Exception {
		//when(emailQueueDAO.findEmailAddressExclusionAlreadyExists((String) any())).thenReturn(false);
		// verify(database).executeInsert((String) any());
	}

	@Test
	public void testRemoveEmailAddressExclusions_invalidEmail() throws Exception {
		when(EmailAddressUtils.isValidEmail((String) any())).thenReturn(false);
		emailQueueDAO.removeEmailAddressExclusions((String) any());
		verify(database, never()).executeUpdate((String) any());
	}

	@Test
	public void testRemoveEmailAddressExclusions_validEmail() throws Exception {
		when(EmailAddressUtils.isValidEmail((String) any())).thenReturn(true);
		// emailQueueDAO.removeEmailAddressExclusions((String) any());
		// verify(database).executeUpdate((String) any());
	}
}
