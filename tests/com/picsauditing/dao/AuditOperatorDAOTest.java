package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class AuditOperatorDAOTest {

	@Autowired
	private AuditOperatorDAO auditoperatorDAO;

	@Test
	public void testSaveAndRemove() {
		AuditOperator auditoperator = new AuditOperator();
		auditoperator.setAuditType(new AuditType());
		auditoperator.getAuditType().setAuditTypeID(9);
		auditoperator.setOperatorAccount(new OperatorAccount());
		auditoperator.getOperatorAccount().setId(228);
		auditoperator.setMinRiskLevel(2);
		auditoperator.setRequiredForFlag(FlagColor.Amber);
		auditoperator.setOrderedCount(1);
		auditoperator.setOrderDate(Calendar.getInstance().getTime());
		auditoperator = auditoperatorDAO.save(auditoperator);
		assertTrue(auditoperator.getAuditOperatorID() > 0);

		List<AuditOperator> testFindByOperator = auditoperatorDAO.findByOperator(228);
		assertTrue(testFindByOperator.size() > 0);

		List<AuditOperator> testFindByAudit = auditoperatorDAO.findByAudit(9);
		assertEquals(228, testFindByAudit.get(0).getOperatorAccount().getId().intValue());

		auditoperatorDAO.remove(auditoperator.getAuditOperatorID());
		AuditOperator auditoperator1 = auditoperatorDAO.find(auditoperator.getAuditOperatorID());
		assertNull(auditoperator1);
	}

	// @Test
	public void testFindByOperator() {
		List<AuditOperator> auditoperator = auditoperatorDAO.findByOperator(228);
		assertTrue(auditoperator.size() > 2);
	}

	// @Test
	public void testFindByAudit() {
		List<AuditOperator> auditoperator = auditoperatorDAO.findByAudit(1);
		assertEquals(451, auditoperator.get(0).getAuditOperatorID());
	}

	// @Test
	public void testFind() {
		AuditOperator auditoperator = auditoperatorDAO.find(451);
		assertEquals(1, auditoperator.getMinRiskLevel());
	}

}
