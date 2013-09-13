package com.picsauditing.dao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.jpa.entities.AppProperty;

public class FeatureToggleDAOTest {
	private FeatureToggleDAO featureToggleDAO;

	@Mock
	private EntityManager em;
	@Mock
	private AppProperty property;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		featureToggleDAO = new FeatureToggleDAO();
		featureToggleDAO.setEntityManager(em);
	}

	@Test
	public void testFindFeatureToggle_Happy() throws Exception {
		when(em.find(eq(AppProperty.class), eq("Toggle.Test"))).thenReturn(property);
		when(property.getValue()).thenReturn("Test");

		assertThat(featureToggleDAO.findFeatureToggle("Toggle.Test"), is(equalTo("Test")));
	}

	@Test
	public void testFindFeatureToggle_TogglePrefixAddedIfNecessary() throws Exception {
		featureToggleDAO.findFeatureToggle("NotPrefixed");

		verify(em).find(eq(AppProperty.class), eq("Toggle.NotPrefixed"));
	}

	@Test
	public void testFindFeatureToggle_NotFoundReturnsNull() throws Exception {
		when(em.find(eq(AppProperty.class), eq("Toggle.Test"))).thenReturn(null);

		assertThat(featureToggleDAO.findFeatureToggle("Toggle.Test"), is(equalTo(null)));
	}

}
