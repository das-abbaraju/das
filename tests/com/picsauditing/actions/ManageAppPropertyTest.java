package com.picsauditing.actions;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.AppPropertyDAO;
import com.picsauditing.jpa.entities.AppProperty;

public class ManageAppPropertyTest extends PicsActionTest {
	private static final String GET = "GET";
	private static final String POST = "POST";

	private ManageAppProperty manageAppProperty;

	@Mock
	private AppPropertyDAO appPropertyDAO;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		manageAppProperty = new ManageAppProperty();
		super.setupMocks();

		Whitebox.setInternalState(manageAppProperty, "appPropertyDAO", appPropertyDAO);
	}

	@Test
	public void testExecute() throws Exception {
		assertEquals("list", manageAppProperty.execute());
	}

	@Test
	public void testList() throws Exception {
		assertEquals("list", manageAppProperty.execute());
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals("create", manageAppProperty.create());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_GET() throws Exception {
		when(request.getMethod()).thenReturn(GET);

		assertEquals("create", manageAppProperty.create());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_GET_ParametersSet() throws Exception {
		when(request.getMethod()).thenReturn(GET);

		manageAppProperty.setNewProperty("Hey");
		manageAppProperty.setNewValue("Universe");
		manageAppProperty.setProperty(new AppProperty());
		manageAppProperty.getProperty().setProperty("Hello");
		manageAppProperty.getProperty().setValue("World");

		assertEquals("create", manageAppProperty.create());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("World", manageAppProperty.getProperty().getValue());
		assertEquals("Hey", manageAppProperty.getNewProperty());
		assertEquals("Universe", manageAppProperty.getNewValue());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_POST_MissingProperty() throws Exception {
		when(request.getMethod()).thenReturn(POST);

		assertEquals("create", manageAppProperty.create());
		assertTrue(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_POST_EmptyProperty() throws Exception {
		manageAppProperty.setNewProperty("");

		when(request.getMethod()).thenReturn(POST);

		assertEquals("create", manageAppProperty.create());
		assertTrue(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_POST_MissingValue() throws Exception {
		manageAppProperty.setNewProperty("Hello");

		when(request.getMethod()).thenReturn(POST);

		assertEquals("create", manageAppProperty.create());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_POST_EmptyValue() throws Exception {
		manageAppProperty.setNewProperty("Hello");
		manageAppProperty.setNewValue("");

		when(request.getMethod()).thenReturn(POST);

		assertEquals("create", manageAppProperty.create());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_POST_Save_Successful() throws Exception {
		manageAppProperty.setNewProperty("Hello");
		manageAppProperty.setNewValue("World");

		when(request.getMethod()).thenReturn(POST);

		assertEquals("create", manageAppProperty.create());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("World", manageAppProperty.getProperty().getValue());

		verify(appPropertyDAO, only()).save(any(AppProperty.class));
	}

	@Test
	public void testCreate_POST_Save_RedirectSave() throws Exception {
		manageAppProperty.setNewProperty("Hello");
		manageAppProperty.setNewValue("World");

		when(request.getMethod()).thenReturn(POST);
		when(request.getParameter(ManageAppProperty.SAVE)).thenReturn(ManageAppProperty.SAVE);

		assertEquals(PicsActionSupport.REDIRECT, manageAppProperty.create());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("World", manageAppProperty.getProperty().getValue());

		verify(appPropertyDAO, only()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit() throws Exception {
		assertEquals("edit", manageAppProperty.edit());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_GET() throws Exception {
		when(request.getMethod()).thenReturn(GET);

		assertEquals("edit", manageAppProperty.edit());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_GET_ParametersSet() throws Exception {
		when(request.getMethod()).thenReturn(GET);

		manageAppProperty.setProperty(new AppProperty());
		manageAppProperty.getProperty().setProperty("Hello");
		manageAppProperty.getProperty().setValue("World");
		manageAppProperty.setNewValue("Universe");

		assertEquals("edit", manageAppProperty.edit());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("World", manageAppProperty.getProperty().getValue());
		assertEquals("Universe", manageAppProperty.getNewValue());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_POST_NoParameters() throws Exception {
		when(request.getMethod()).thenReturn(POST);

		assertEquals("edit", manageAppProperty.edit());
		assertTrue(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_POST_MissingNewValue() throws Exception {
		when(request.getMethod()).thenReturn(POST);

		manageAppProperty.setProperty(new AppProperty());
		manageAppProperty.getProperty().setProperty("Hello");
		manageAppProperty.getProperty().setValue("World");

		assertEquals("edit", manageAppProperty.edit());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("World", manageAppProperty.getProperty().getValue());
		assertTrue(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_POST_SaveValue() throws Exception {
		when(request.getMethod()).thenReturn(POST);

		manageAppProperty.setProperty(new AppProperty());
		manageAppProperty.getProperty().setProperty("Hello");
		manageAppProperty.getProperty().setValue("World");
		manageAppProperty.setNewValue("Universe");

		assertEquals("edit", manageAppProperty.edit());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("Universe", manageAppProperty.getProperty().getValue());
		assertFalse(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, only()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_POST_SaveProperty() throws Exception {
		when(request.getMethod()).thenReturn(POST);

		manageAppProperty.setProperty(new AppProperty());
		manageAppProperty.getProperty().setProperty("Hello");
		manageAppProperty.getProperty().setValue("World");
		manageAppProperty.setNewProperty("Hey");

		assertEquals("edit", manageAppProperty.edit());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("World", manageAppProperty.getProperty().getValue());
		assertTrue(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, never()).save(any(AppProperty.class));
	}

	@Test
	public void testEdit_POST_Save_RedirectSave() throws Exception {
		when(request.getMethod()).thenReturn(POST);
		when(request.getParameter(ManageAppProperty.SAVE)).thenReturn(ManageAppProperty.SAVE);

		manageAppProperty.setProperty(new AppProperty());
		manageAppProperty.getProperty().setProperty("Hello");
		manageAppProperty.getProperty().setValue("World");
		manageAppProperty.setNewValue("Universe");

		assertEquals(PicsActionSupport.REDIRECT, manageAppProperty.edit());
		assertEquals("Hello", manageAppProperty.getProperty().getProperty());
		assertEquals("Universe", manageAppProperty.getProperty().getValue());
		assertFalse(manageAppProperty.hasActionErrors());

		verify(appPropertyDAO, only()).save(any(AppProperty.class));
	}

	@Test
	public void testGetAll() {
		List<AppProperty> all = new ArrayList<AppProperty>();
		AppProperty appProperty = new AppProperty();
		appProperty.setProperty("Hello");
		appProperty.setValue("World");
		all.add(appProperty);

		when(appPropertyDAO.findAll()).thenReturn(all);
		assertEquals(all, manageAppProperty.getAll());
		assertEquals("Hello", all.get(0).getProperty());
		assertEquals("World", all.get(0).getValue());

		verify(appPropertyDAO, only()).findAll();
	}

	@Test
	public void testGetAll_CalledTwice() {
		List<AppProperty> all = new ArrayList<AppProperty>();
		AppProperty appProperty = new AppProperty();
		appProperty.setProperty("Hello");
		appProperty.setValue("World");
		all.add(appProperty);

		when(appPropertyDAO.findAll()).thenReturn(all);
		assertEquals(all, manageAppProperty.getAll());
		assertEquals("Hello", all.get(0).getProperty());
		assertEquals("World", all.get(0).getValue());

		manageAppProperty.getAll();

		verify(appPropertyDAO, only()).findAll();
	}
}