package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.model.i18n.LanguageModel;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class DialectActionTest extends PicsActionTest {

	@Mock
	private LanguageModel languageModel;

	private DialectAction dialectAction;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		dialectAction = new DialectAction();

		super.setUp(dialectAction);

		Whitebox.setInternalState(dialectAction, languageModel);
	}

	@Test
	public void testIndex() throws Exception {
		dialectAction.setLanguage("en");

		when(languageModel.getCountriesBasedOn("en")).thenReturn(new HashMap<String, String>() {{

			put("AU", "Australia");
			put("CA", "Canada");
			put("US", "United States");

		}});

		String result = dialectAction.index();

		assertEquals(PicsActionSupport.JSON_STRING, result);
		Approvals.verify(dialectAction.getJsonString());
	}
}
