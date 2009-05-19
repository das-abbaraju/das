package com.picsauditing.dao;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
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

		ContractorAudit ca = auditDao.findActiveByContractor(3, 14);

		cao.setOperator(op);
		cao.setAudit(ca);
		cao.setRecommendedStatus(CaoStatus.Approved);
		cao.setStatus(CaoStatus.Approved);
		cao.setAuditColumns(new User(2357)); // kpartridge

		cao = caoDao.save(cao);
		assertTrue(cao.getId() > 0);

		caoDao.remove(cao.getId());
		assertNull(caoDao.find(cao.getId()));
	}
}
