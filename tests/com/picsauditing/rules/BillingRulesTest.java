package com.picsauditing.rules;

import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.picsauditing.PicsTest;
import com.picsauditing.PICS.DroolsSessionFactory;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

import edu.emory.mathcs.backport.java.util.Arrays;

public class BillingRulesTest extends PicsTest {
	@Autowired
	@Qualifier("BillingDroolsSessionFactory")
	private DroolsSessionFactory droolsSessionFactory;

	@Autowired
	private ContractorAccountDAO conDao;

	private StatelessKnowledgeSession statelessSession;

	@Before
	public void createStatelessSession() {
		statelessSession = droolsSessionFactory.getStatelessSession();
	}

	@Test
	public void test() throws Exception {
		ContractorAccount con = conDao.find(3);
		ContractorAccount con2 = conDao.find(14);

		statelessSession.execute(Arrays.asList(new Object[] { con, con2 }));
	}
}
