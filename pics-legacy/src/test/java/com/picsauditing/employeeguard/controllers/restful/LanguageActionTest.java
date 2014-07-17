package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.model.i18n.KeyValue;
import com.picsauditing.model.i18n.LanguageModel;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UseReporter(DiffReporter.class)
public class LanguageActionTest extends PicsActionTest {

	@Mock
	private LanguageModel languageModel;

	// Class under test
	private LanguageAction languageAction;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		languageAction = new LanguageAction();

		super.setUp(languageAction);

		Whitebox.setInternalState(languageAction, languageModel);
	}

	@Test
	public void testIndex() throws Exception {
		setupTestIndex();

		String result = languageAction.index();

		verifyTestIndex(result);
	}

	private void setupTestIndex() {
		when(languageModel.getVisibleLanguagesSansDialect()).thenReturn(new ArrayList<KeyValue<String, String>>() {{

			add(new KeyValue<>("en", "English"));
			add(new KeyValue<>("de", "Deutsch"));
			add(new KeyValue<>("nl", "Nederlands"));

		}});
	}

	private void verifyTestIndex(String result) throws Exception {
		assertEquals(PicsActionSupport.JSON_STRING, result);

		Approvals.verify(languageAction.getJsonString());
	}
}
