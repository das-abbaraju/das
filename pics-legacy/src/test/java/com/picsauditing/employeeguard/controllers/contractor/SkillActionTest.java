package com.picsauditing.employeeguard.controllers.contractor;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.controller.PicsRestActionSupport;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.forms.SearchForm;
import com.picsauditing.employeeguard.forms.contractor.SkillForm;
import com.picsauditing.employeeguard.services.SkillService;
import com.picsauditing.employeeguard.services.factory.GroupServiceFactory;
import com.picsauditing.employeeguard.services.factory.SkillServiceFactory;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.web.UrlBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillActionTest extends PicsActionTest {
	public static final String EDIT_FORM = "edit-form";
	public static final String ID = "ID";
	public static final String TEST = "Test";
	private SkillAction skillAction;

	private SkillService skillService;

	@Mock
	private UrlBuilder urlBuilder;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		skillAction = new SkillAction();
		skillService = SkillServiceFactory.getSkillService();
		super.setUp(skillAction);

		Whitebox.setInternalState(skillAction, "skillService", skillService);
		Whitebox.setInternalState(skillAction, "groupService", GroupServiceFactory.getGroupService());
		Whitebox.setInternalState(skillAction, "urlBuilder", urlBuilder);

		when(permissions.getAccountId()).thenReturn(Account.PicsID);
		when(permissions.getAppUserID()).thenReturn(Identifiable.SYSTEM);
	}

	@Test
	public void testIndex() throws Exception {
		String result = skillAction.index();
		assertEquals(PicsRestActionSupport.LIST, result);
		assertFalse(skillAction.getSkills().isEmpty());
		verify(skillService).getSkillsForAccount(Account.PicsID);
	}

	@Test
	public void testIndex_Search() throws Exception {
		SearchForm searchForm = new SearchForm();
		searchForm.setSearchTerm(TEST);
		skillAction.setSearchForm(searchForm);

		String result = skillAction.index();
		assertEquals(PicsRestActionSupport.LIST, result);
		assertFalse(skillAction.getSkills().isEmpty());
		verify(skillService).search(TEST, Account.PicsID);
	}

	@Test
	public void testShow() throws Exception {
		skillAction.setId(ID);

		assertEquals(PicsRestActionSupport.SHOW, skillAction.show());
		assertNotNull(skillAction.getSkill());
		verify(skillService).getSkill(ID, Account.PicsID);
	}

	@Test
	public void testCreate() throws Exception {
		assertEquals(PicsRestActionSupport.CREATE, skillAction.create());
		assertFalse(skillAction.getSkillGroups().isEmpty());
	}

	@Test
	public void testCreate_Ajax() throws Exception {
		when(request.getHeader("X-Requested-With")).thenReturn(AjaxUtils.AJAX_REQUEST_HEADER_VALUE);

		assertEquals("create-form", skillAction.create());
		assertFalse(skillAction.getSkillGroups().isEmpty());
	}

	@Test
	public void testEditSkillSection() throws Exception {
		skillAction.setSkillForm(new SkillForm());

		assertEquals(EDIT_FORM, skillAction.editSkillSection());
		assertNotNull(skillAction.getSkill());
		assertFalse(skillAction.getSkillGroups().isEmpty());
	}

	@Test
	public void testEditSkillSection_SkillFormIsNull() throws Exception {
		assertEquals(EDIT_FORM, skillAction.editSkillSection());
		assertNotNull(skillAction.getSkill());
		assertNotNull(skillAction.getSkillForm());
		assertFalse(skillAction.getSkillGroups().isEmpty());
	}

	@Test
	public void testInsert() throws Exception {
		String url = "/employee-guard/contractor/skill";
		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		skillAction.setSkillForm(new SkillForm());

		assertEquals(PicsActionSupport.REDIRECT, skillAction.insert());
		verify(skillService).save(any(AccountSkill.class), anyInt(), anyInt());
		assertEquals(url, skillAction.getUrl());
	}

	@Test
	public void testInsert_AddAnother() throws Exception {
		SkillForm skillForm = new SkillForm();
		skillForm.setAddAnother(true);

		skillAction.setSkillForm(skillForm);

		assertEquals(PicsActionSupport.REDIRECT, skillAction.insert());
		verify(permissions).getAccountId();
		verify(permissions).getAppUserID();
		verify(skillService).save(any(AccountSkill.class), anyInt(), anyInt());
		assertEquals("/employee-guard/contractor/skill/create", skillAction.getUrl());
	}

	@Test
	public void testUpdate() throws Exception {
		skillAction.setSkillForm(new SkillForm());

		assertEquals(PicsActionSupport.REDIRECT, skillAction.update());
		verify(permissions).getAccountId();
		verify(permissions).getAppUserID();
		verify(skillService).update(any(AccountSkill.class), anyString(), anyInt(), anyInt());
		assertTrue(skillAction.getUrl().startsWith("/employee-guard/contractor/skill/"));
	}

	@Test
	public void testDelete() throws Exception {
		String url = "/employee-guard/contractor/skill";
		when(urlBuilder.action(anyString())).thenReturn(urlBuilder);
		when(urlBuilder.build()).thenReturn(url);

		assertEquals(PicsActionSupport.REDIRECT, skillAction.delete());
		verify(permissions).getAccountId();
		verify(permissions).getAppUserID();
		verify(skillService).delete(anyString(), anyInt(), anyInt());
		assertEquals(url, skillAction.getUrl());
	}
}
