package com.picsauditing.actions;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class HomeTest extends PicsActionTest {

	private Home home;

	@Before
	public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        home = new Home();
        super.setUp(home);
    }

    @Test
    public void testExecute_noV6Dash() throws Exception {
        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.hasPermission(OpPerms.Dashboard)).thenReturn(false);
        when(permissions.isUsingVersion7Menus()).thenReturn(false);
        assertEquals("redirect", home.execute());
        assertEquals("ContractorList.action?filter.performedBy=Self%20Performed", home.getUrl());
    }

    @Test
    public void testExecute_noV7Dash() throws Exception {
        when(permissions.isOperatorCorporate()).thenReturn(true);
        when(permissions.hasPermission(OpPerms.Dashboard)).thenReturn(false);
        when(permissions.isUsingVersion7Menus()).thenReturn(true);
        assertEquals("redirect", home.execute());
        assertEquals("Report.action?report=100", home.getUrl());
    }
}