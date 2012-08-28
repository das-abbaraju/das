package com.picsauditing.actions;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.search.Database;

public class DataConversionRequestAccountTest {
	private DataConversionRequestAccount requestConversion;
	private PicsTestUtil picsTestUtil;

	@Mock
	private Database database;
	@Mock
	private EntityManager entityManager;
	@Mock
	private Query query;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", database);

		requestConversion = new DataConversionRequestAccount();
		picsTestUtil = new PicsTestUtil();

		picsTestUtil.autowireEMInjectedDAOs(requestConversion, entityManager);
	}

	@AfterClass
	public static void classTearDown() {
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", (Database) null);
	}

	@Test
	public void testExecute() {
		assertEquals(PicsActionSupport.SUCCESS, requestConversion.execute());
	}

	@Test
	public void testNeedsUpgrade() {
		when(entityManager.createQuery(anyString())).thenReturn(query);

		verify(entityManager).createQuery(anyString());
		verify(entityManager, never()).merge(any(BaseTable.class));
		verify(entityManager, never()).persist(any(BaseTable.class));
		verify(query).getResultList();
	}
}
