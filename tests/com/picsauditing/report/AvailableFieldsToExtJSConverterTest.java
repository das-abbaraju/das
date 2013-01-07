package com.picsauditing.report;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.report.models.AbstractModel;
import com.picsauditing.report.models.ModelFactory;
import com.picsauditing.report.models.ModelType;
import com.picsauditing.search.Database;

public class AvailableFieldsToExtJSConverterTest {
	@Mock
	private Permissions permissions;
	@Mock
	protected I18nCache i18nCache;
	@Mock
	private Database databaseForTesting;

	private final int USER_ID = 123;
	private final int ACCOUNT_ID = Account.PicsID;

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);

		when(permissions.getAccountIdString()).thenReturn("" + ACCOUNT_ID);
		when(permissions.getVisibleAccounts()).thenReturn(new HashSet<Integer>());
		when(permissions.getUserIdString()).thenReturn("" + USER_ID);
		when(permissions.getUserId()).thenReturn(USER_ID);
		when(permissions.getLocale()).thenReturn(Locale.ENGLISH);
	}

	@Test
	public void testColumns() {
		AbstractModel model = ModelFactory.build(ModelType.Accounts, permissions);
		JSONArray actual = AvailableFieldsToExtJSConverter.getColumns(model, permissions);

		assertTrue(actual.size() > 30);
	}

}
