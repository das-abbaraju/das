package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import com.picsauditing.employeeguard.entities.ProfileDocument;
import com.picsauditing.employeeguard.entities.builders.ProfileDocumentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static com.picsauditing.employeeguard.services.entity.EntityAuditInfoConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentEntityServiceTest {

	private DocumentEntityService documentEntityService;

	@Mock
	private ProfileDocumentDAO documentDAO;

	@Before
	public void setUp() throws Exception {
		documentEntityService = new DocumentEntityService();

		MockitoAnnotations.initMocks(this);

		org.mockito.internal.util.reflection.Whitebox.setInternalState(documentEntityService, "documentDAO", documentDAO);
	}

	@Test(expected = NullPointerException.class)
	public void testFind_NullId() throws Exception {
		documentEntityService.find(null);
	}

	@Test
	public void testFind() throws Exception {
		ProfileDocument expected = buildFakeProfileDocument();

		when(documentDAO.find(expected.getId())).thenReturn(expected);

		ProfileDocument result = documentEntityService.find(expected.getId());

		assertNotNull(result);
		assertEquals(expected.getId(), result.getId());
	}

	@Test
	public void testSearch_NullEmpty() throws Exception {
		List<ProfileDocument> nullSearch = documentEntityService.search(null, ACCOUNT_ID);
		assertNotNull(nullSearch);
		assertTrue(nullSearch.isEmpty());

		List<ProfileDocument> emptySearch = documentEntityService.search(" ", ACCOUNT_ID);
		assertNotNull(emptySearch);
		assertTrue(emptySearch.isEmpty());
	}

	@Test
	public void testSearch() throws Exception {
		when(documentDAO.search(SEARCH_TERM, ACCOUNT_ID)).thenReturn(Arrays.asList(buildFakeProfileDocument()));

		List<ProfileDocument> result = documentEntityService.search(SEARCH_TERM, ACCOUNT_ID);

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertEquals(ENTITY_ID, result.get(0).getId());
	}

	@Test
	public void testSave() throws Exception {
		ProfileDocument fakeDocument = buildFakeProfileDocument();

		when(documentDAO.save(fakeDocument)).thenReturn(fakeDocument);

		ProfileDocument result = documentEntityService.save(fakeDocument, CREATED);

		verify(documentDAO).save(fakeDocument);
		assertEquals(USER_ID, result.getCreatedBy());
		assertEquals(CREATED_DATE, result.getCreatedDate());
		assertNull(result.getUpdatedDate());
	}

	@Test
	public void testUpdate() throws Exception {
		ProfileDocument fakeDocument = buildFakeProfileDocument();
		fakeDocument.setFileName("File name");

		ProfileDocument updatedDocument = buildFakeProfileDocument();

		when(documentDAO.find(updatedDocument.getId())).thenReturn(updatedDocument);
		when(documentDAO.save(updatedDocument)).thenReturn(updatedDocument);

		ProfileDocument result = documentEntityService.update(fakeDocument, UPDATED);

		verify(documentDAO).find(updatedDocument.getId());
		verify(documentDAO).save(updatedDocument);
		assertEquals(updatedDocument.getId(), result.getId());
		assertEquals(fakeDocument.getFileName(), result.getFileName());
		assertEquals(USER_ID, result.getUpdatedBy());
		assertEquals(UPDATED_DATE, result.getUpdatedDate());
	}

	@Test
	public void testDelete() throws Exception {
		ProfileDocument fakeDocument = buildFakeProfileDocument();

		documentEntityService.delete(fakeDocument);

		verify(documentDAO).delete(fakeDocument);
	}

	@Test
	public void testDeleteById() throws Exception {
		ProfileDocument fakeDocument = buildFakeProfileDocument();

		when(documentDAO.find(fakeDocument.getId())).thenReturn(fakeDocument);

		documentEntityService.deleteById(fakeDocument.getId());

		verify(documentDAO).delete(fakeDocument);
	}

	private ProfileDocument buildFakeProfileDocument() {
		return new ProfileDocumentBuilder()
				.id(ENTITY_ID)
				.build();
	}
}
