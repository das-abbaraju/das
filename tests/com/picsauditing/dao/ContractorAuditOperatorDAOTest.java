package com.picsauditing.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class ContractorAuditOperatorDAOTest {

	@Autowired
	ContractorAuditOperatorDAO caoDao;
	@Autowired
	ContractorAuditDAO auditDao;

	@Test
	public void testSaveAndRemove() {
		ContractorAuditOperator cao = new ContractorAuditOperator();

		OperatorAccount op = new OperatorAccount();
		op.setId(16);

		ContractorAudit ca = auditDao.findActiveByContractor(3, 1);

		assertNotNull("Audit should not be null", ca);

		cao.setOperator(op);
		cao.setAudit(ca);
		cao.setFlag(FlagColor.Green);
		cao.setStatus(CaoStatus.Approved);
		cao.setAuditColumns(new User(2357)); // kpartridge

		cao = caoDao.save(cao);
		assertTrue(cao.getId() > 0);

		caoDao.remove(cao.getId());
		assertNull(caoDao.find(cao.getId()));
	}

	@Test
	public void testFindCaosByContractor() {
		try {
			User u = new User("Couch"); // Ancon Marine (Contractor)

			Permissions perm = new Permissions();
			perm.login(u);
			List<ContractorAuditOperator> caoList = caoDao.findByContractorAccount(3, perm);

			assertNotNull(caoList);
			assertTrue(caoList.size() > 0);

			for (ContractorAuditOperator cao : caoList)
				assertTrue(u.getAccount().getId() == cao.getAudit().getContractorAccount().getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testFindCaosByContractorWithOperatorPermissions() {
		try {
			User u = new User("rgraves@ppcla.com"); // Paramount (Operator)

			Permissions perm = new Permissions();
			perm.login(u);
			List<ContractorAuditOperator> caoList = caoDao.findByContractorAccount(3, perm);

			assertNotNull(caoList);
			assertTrue(caoList.size() > 0);

			for (ContractorAuditOperator cao : caoList)
				assertTrue(u.getAccount().getId() == cao.getOperator().getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
