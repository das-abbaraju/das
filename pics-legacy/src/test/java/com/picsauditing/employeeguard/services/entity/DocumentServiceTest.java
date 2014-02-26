package com.picsauditing.employeeguard.services.entity;

import com.picsauditing.employeeguard.daos.ProfileDocumentDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.fail;

public class DocumentServiceTest {

	DocumentService documentService;

	@Mock
	private ProfileDocumentDAO profileDocumentDAO;

	@Before
	public void setUp() throws Exception {
		documentService = new DocumentService();

		MockitoAnnotations.initMocks(this);

		Whitebox.setInternalState(documentService, "documentDAO", profileDocumentDAO);
	}

	@Test
	public void testFind() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testSearch() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testSave() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testUpdate() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testDelete() throws Exception {
		fail("Not implemented.");
	}

	@Test
	public void testDeleteById() throws Exception {
		fail("Not implemented.");
	}
}
