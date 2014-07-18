package com.picsauditing.service.user;

import com.picsauditing.authentication.entities.AppUser;
import com.picsauditing.authentication.service.AppUserService;
import com.picsauditing.dao.IdpUserDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class IdpUserServiceTest {

    public static final String IDPUSERNAME = "idpusername";
    public static final String IDP = "idp";
    IdpUserService idpUserService = new IdpUserService();

	@Mock
	private User user;

    @Mock
    private IdpUser idpUser;

	@Mock
	private IdpUserDAO idpUserDAO;
    @Mock
    private AppUserService appUserService;
    @Mock
    private AppUser appUser;
	@Mock
	private Account account;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(idpUserService, "idpUserDAO", idpUserDAO);
	}

	@Test
	public void testLoadIdpUser() throws Exception {
        when(idpUserDAO.find(anyInt())).thenReturn(idpUser);

		IdpUser result = idpUserService.loadIdpUser(1);

		assertTrue(result != null);
	}

    @Test
    public void testLoadIdpUserBy() throws Exception {
        when(idpUserDAO.find(anyInt())).thenReturn(idpUser);
        when(idpUserDAO.findBy(IDPUSERNAME, IDP)).thenReturn(idpUser);

        IdpUser result = idpUserService.loadIdpUserBy(IDPUSERNAME, IDP);

        assertTrue(result!=null);
    }

    @Test
    public void testSaveUser() throws Exception {
        idpUserService.saveIdpUser(idpUser);

        verify(idpUserDAO).save(idpUser);
    }
}
