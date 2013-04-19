package com.picsauditing.util.i18n;

import com.picsauditing.jpa.entities.BaseTableRequiringLanguages;
import com.picsauditing.jpa.entities.RequiresLanguages;
import com.picsauditing.util.Strings;
import org.json.simple.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class RequiredLanguageTransformerTest {
	private RequiredLanguageTransformer transformer;

	@Mock
	private BaseTableRequiringLanguages baseTableRequiringLanguages;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		transformer = new RequiredLanguageTransformer();
	}

	@Test
	public void testGetListFromJSON_EmptyOrNull() throws Exception {
		assertNotNull(transformer.getListFromJSON(""));
		assertTrue(transformer.getListFromJSON("").isEmpty());

		assertNotNull(transformer.getListFromJSON(null));
		assertTrue(transformer.getListFromJSON(null).isEmpty());
	}

	@Test
	public void testGetListFromJSON() throws Exception {
		JSONArray jsonArray = new JSONArray();
		jsonArray.add("Test");

		List<String> listFromJSON = transformer.getListFromJSON(jsonArray.toJSONString());
		assertNotNull(listFromJSON);
		assertEquals(1, listFromJSON.size());
		assertEquals("Test", listFromJSON.get(0));
	}

	@Test
	public void testGetJSONStringFrom_EmptyOrNullString() throws Exception {
		assertEquals(Strings.EMPTY_STRING, transformer.getJSONStringFrom(null));
		assertEquals(Strings.EMPTY_STRING, transformer.getJSONStringFrom(new ArrayList<String>()));
	}

	@Test
	public void testGetJSONStringFrom() throws Exception {
		JSONArray jsonArray = new JSONArray();
		jsonArray.add("Test");

		List<String> languages = new ArrayList<>();
		languages.add("Test");

		assertEquals(jsonArray.toJSONString(), transformer.getJSONStringFrom(languages));
	}

	@Test
	public void testUpdateRequiredLanguages_AddOrRemoveListNull() throws Exception {
		List<String> languages = new ArrayList<>();
		languages.add("Test 1");

		List<String> add = new ArrayList<>();
		add.add("Test 2");

		List<String> remove = new ArrayList<>();
		remove.add("Test 1");

		when(baseTableRequiringLanguages.getLanguages()).thenReturn(languages);
		ArgumentCaptor<List> languagesCaptor = ArgumentCaptor.forClass(List.class);

		transformer.updateRequiredLanguages(baseTableRequiringLanguages, add, null);

		verify(baseTableRequiringLanguages).setLanguages(languagesCaptor.capture());
		assertNotNull(languagesCaptor.getValue());
		assertEquals(2, languagesCaptor.getValue().size());
		assertEquals("Test 1", languagesCaptor.getValue().get(0));
		assertEquals("Test 2", languagesCaptor.getValue().get(1));

		transformer.updateRequiredLanguages(baseTableRequiringLanguages, null, remove);

		// Same mock gets hit twice
		verify(baseTableRequiringLanguages, times(2)).setLanguages(languagesCaptor.capture());
		assertNotNull(languagesCaptor.getValue());
		assertTrue(languagesCaptor.getValue().isEmpty());
	}

	@Test
	public void testUpdateRequiredLanguages_EntityLanguagesIsNull() throws Exception {
		List<String> add = new ArrayList<>();
		add.add("Test 2");

		List<String> remove = new ArrayList<>();
		remove.add("Test 1");

		ArgumentCaptor<List> languagesCaptor = ArgumentCaptor.forClass(List.class);

		transformer.updateRequiredLanguages(baseTableRequiringLanguages, add, remove);

		verify(baseTableRequiringLanguages).setLanguages(languagesCaptor.capture());
		assertNotNull(languagesCaptor.getValue());
		assertEquals(1, languagesCaptor.getValue().size());
		assertEquals("Test 2", languagesCaptor.getValue().get(0));
	}

	@Test
	public void testUpdateRequiredLanguages_Happy() throws Exception {
		List<String> languages = new ArrayList<>();
		languages.add("Test 1");

		List<String> add = new ArrayList<>();
		add.add("Test 2");

		List<String> remove = new ArrayList<>();
		remove.add("Test 1");

		when(baseTableRequiringLanguages.getLanguages()).thenReturn(languages);
		ArgumentCaptor<List> languagesCaptor = ArgumentCaptor.forClass(List.class);

		transformer.updateRequiredLanguages(baseTableRequiringLanguages, add, remove);

		verify(baseTableRequiringLanguages).setLanguages(languagesCaptor.capture());

		assertNotNull(languagesCaptor.getValue());
		assertEquals(1, languagesCaptor.getValue().size());
		assertEquals("Test 2", languagesCaptor.getValue().get(0));
	}

	@Test
	 public void testGetLanguagesToAdd_SourceIsNull() throws Exception {
		List<String> target = new ArrayList<>();
		target.add("Test");

		List<String> languagesToAdd = transformer.getLanguagesToAdd(null, target);
		assertNotNull(languagesToAdd);
		assertEquals(1, languagesToAdd.size());
		assertEquals("Test", languagesToAdd.get(0));
	}

	@Test
	public void testGetLanguagesToAdd_TargetIsNull() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Test");

		List<String> languagesToAdd = transformer.getLanguagesToAdd(source, null);
		assertNotNull(languagesToAdd);
		assertTrue(languagesToAdd.isEmpty());
	}

	@Test
	public void testGetLanguagesToAdd_TargetIsSameAsSource() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Test");

		List<String> target = new ArrayList<>();
		target.add("Test");

		List<String> languagesToAdd = transformer.getLanguagesToAdd(source, target);
		assertNotNull(languagesToAdd);
		assertTrue(languagesToAdd.isEmpty());
	}

	@Test
	public void testGetLanguagesToAdd_TargetIsNotSameAsSource() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Test 1");

		List<String> target = new ArrayList<>();
		target.add("Test 1");
		target.add("Test 2");

		List<String> languagesToAdd = transformer.getLanguagesToAdd(source, target);
		assertNotNull(languagesToAdd);
		assertFalse(languagesToAdd.isEmpty());
		assertEquals(1, languagesToAdd.size());
		assertEquals("Test 2", languagesToAdd.get(0));
	}

	@Test
	public void testGetLanguagesToRemove_SourceIsNull() throws Exception {
		List<String> target = new ArrayList<>();
		target.add("Test 1");

		List<String> languagesToRemove = transformer.getLanguagesToRemove(null, target);
		assertNotNull(languagesToRemove);
		assertTrue(languagesToRemove.isEmpty());
	}

	@Test
	public void testGetLanguagesToRemove_TargetIsNull() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Test");

		List<String> languagesToRemove = transformer.getLanguagesToRemove(source, null);
		assertNotNull(languagesToRemove);
		assertFalse(languagesToRemove.isEmpty());
		assertEquals(1, languagesToRemove.size());
		assertEquals("Test", languagesToRemove.get(0));
	}

	@Test
	public void testGetLanguagesToRemove_TargetIsSameAsSource() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Test");

		List<String> target = new ArrayList<>();
		target.add("Test");

		List<String> languagesToRemove = transformer.getLanguagesToRemove(source, target);
		assertNotNull(languagesToRemove);
		assertTrue(languagesToRemove.isEmpty());
	}

	@Test
	public void testGetLanguagesToRemove_TargetIsNotSameAsSource() throws Exception {
		List<String> source = new ArrayList<>();
		source.add("Test 1");
		source.add("Test 2");

		List<String> target = new ArrayList<>();
		target.add("Test 1");

		List<String> languagesToRemove = transformer.getLanguagesToRemove(source, target);
		assertNotNull(languagesToRemove);
		assertFalse(languagesToRemove.isEmpty());
		assertEquals(1, languagesToRemove.size());
		assertEquals("Test 2", languagesToRemove.get(0));
	}
}
