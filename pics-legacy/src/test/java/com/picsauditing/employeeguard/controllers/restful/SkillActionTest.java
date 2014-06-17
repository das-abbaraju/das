package com.picsauditing.employeeguard.controllers.restful;

import com.picsauditing.PicsActionTest;
import com.picsauditing.database.domain.Identifiable;
import com.picsauditing.employeeguard.EGTestDataUtil;
import com.picsauditing.employeeguard.controllers.contractor.*;
import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillRole;
import com.picsauditing.employeeguard.entities.Role;
import com.picsauditing.employeeguard.entities.builders.AccountSkillBuilder;
import com.picsauditing.employeeguard.services.AccountService;
import com.picsauditing.employeeguard.services.CorpOpSkillService;
import com.picsauditing.employeeguard.services.entity.SkillEntityService;
import com.picsauditing.employeeguard.services.factory.GroupServiceFactory;
import com.picsauditing.employeeguard.services.factory.SkillServiceFactory;
import com.picsauditing.jpa.entities.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SkillActionTest extends PicsActionTest {
	@Mock
	private AccountService accountService;

	@Mock
	CorpOpSkillService corpOpSkillService;

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
		Whitebox.setInternalState(skillAction, "corpOpSkillService", corpOpSkillService);
		Whitebox.setInternalState(corpOpSkillService, "skillEntityService", skillEntityService);

		egTestDataUtil = new EGTestDataUtil();
	}


	@Test
	public void testFindSkillsForCorpOp() throws Exception {
		skillAction.findSkillsForCorpOp();

		verify(corpOpSkillService).findSkillsForCorpOp(anyList(),anyInt());

	}

	@Test
	public void testFilterSkillsForCorpOp() throws Exception {
		String searchTerm="Training";
		skillAction.setFilter("{\"name\":\""+searchTerm+"\"}");
		skillAction.findSkillsForCorpOp();

		verify(corpOpSkillService).filterSkillsForCorpOp(anyString(),anyList(),anyInt());

	}

}
