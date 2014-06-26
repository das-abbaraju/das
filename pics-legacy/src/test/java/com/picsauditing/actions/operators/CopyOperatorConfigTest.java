package com.picsauditing.actions.operators;

import com.picsauditing.PicsActionTest;
import com.picsauditing.dao.OperatorAccountDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

public class CopyOperatorConfigTest extends PicsActionTest {
    private static final int SOURCE_ID = 1;
    private static final int TARGET_ID = 2;
    private static final int USER_ID = 3;

	private CopyOperatorConfig copyOperatorConfig;

	@Mock
	private OperatorAccountDAO operatorAccountDAO;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

        copyOperatorConfig = new CopyOperatorConfig();
        super.setUp(copyOperatorConfig);

        Whitebox.setInternalState(copyOperatorConfig, "operatorAccountDAO", operatorAccountDAO);
        when(permissions.getUserId()).thenReturn(USER_ID);
	}

    @Test
    public void testCopy() throws Exception {
        Whitebox.setInternalState(copyOperatorConfig, "sourceID", SOURCE_ID);
        Whitebox.setInternalState(copyOperatorConfig, "targetID", TARGET_ID);

        String status = copyOperatorConfig.copy();
        assertEquals("success", status);
        verify(operatorAccountDAO).copyAuditTypeRules(SOURCE_ID, TARGET_ID, USER_ID);
        verify(operatorAccountDAO).copyAuditCategoryRules(SOURCE_ID, TARGET_ID, USER_ID);
        verify(operatorAccountDAO).copyFlagCriteriaOperators(SOURCE_ID, TARGET_ID, USER_ID);
    }

}
