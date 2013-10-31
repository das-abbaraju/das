package com.picsauditing.actions.auditType;

import com.picsauditing.PicsTestUtil;
import com.picsauditing.PicsTranslationTest;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.importpqf.ImportStopAt;
import com.picsauditing.jpa.entities.*;
import com.picsauditing.util.SlugService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class ManageAuditTypeTest extends PicsTranslationTest {

	private ManageAuditType manageAuditType;

    @Mock
    private SlugService slugService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);


        manageAuditType = new ManageAuditType();

        Whitebox.setInternalState(manageAuditType, "slugService", slugService);
	}

    @Test
    public void testValidateSlug() {
        when(slugService.slugHasDuplicate(AuditType.class,"manual-audit",0)).thenReturn(true);
        when(slugService.slugIsURICompliant("manual-audit")).thenReturn(true);

        Whitebox.setInternalState(manageAuditType, "slug", "manual-audit");

        String response = manageAuditType.validateSlug();
        assertEquals("json",response);
        assertEquals("{\"isURI\":true,\"isUnique\":false}",manageAuditType.getJson().toString());
    }

    @Test
    public void testGenerateSlug() throws Exception {
        when(slugService.generateSlug(AuditType.class,"Manual Audit",0)).thenReturn("manual-audit");

        Whitebox.setInternalState(manageAuditType, "stringToSlugify", "Manual Audit");

        String response = manageAuditType.generateSlug();
        assertEquals("json",response);
        assertEquals("{\"slug\":\"manual-audit\"}",manageAuditType.getJson().toString());
    }
}
