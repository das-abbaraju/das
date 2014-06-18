package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.CorpSiteSkillService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;

public class SkillActionTest extends PicsActionTest {
	@Mock
	private AccountService accountService;

	@Mock
	CorpSiteSkillService corpSiteSkillService;

	@Mock
	private SkillEntityService skillEntityService;

	private SkillAction skillAction;
	private EGTestDataUtil egTestDataUtil;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		skillAction = new SkillAction();
		super.setUp(skillAction);

		Whitebox.setInternalState(skillAction, "accountService", accountService);
		Whitebox.setInternalState(skillAction, "corpSiteSkillService", corpSiteSkillService);
		Whitebox.setInternalState(corpSiteSkillService, "skillEntityService", skillEntityService);

		egTestDataUtil = new EGTestDataUtil();
	}


	@Test
	public void testFindSkillsForCorpOp() throws Exception {
		skillAction.findSkillsForCorpSite();

		verify(corpSiteSkillService).findSkillsForCorpSite(anyList(), anyInt());

	}

}
