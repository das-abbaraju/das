package com.picsauditing.dao;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagQuestionCriteria;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public class FlagQuestionCriteriaDAOTest {

	@Autowired
	private FlagQuestionCriteriaDAO dao;

	@Test
	public void testSave() {
		FlagQuestionCriteria criteria = new FlagQuestionCriteria();
		criteria.setOperatorAccount(new OperatorAccount());
		criteria.getOperatorAccount().setId(16); // BP Carson
		criteria.setFlagColor(FlagColor.Red);
		criteria.setAuditQuestion(new AuditQuestion());
		criteria.getAuditQuestion().setId(55); // SIC Code
		criteria.setComparison("=");
		criteria.setValue("48409432");
		criteria.setAuditColumns(new User(User.SYSTEM));
		
		try {
			Map<FlagColor, FlagQuestionCriteria> oldCriteriaMap = dao.find(criteria.getOperatorAccount().getId(), criteria.getAuditQuestion().getId());
			for(FlagQuestionCriteria oldCriteria : oldCriteriaMap.values()) {
				dao.remove(oldCriteria.getId());
			}
		} catch (Exception nothing) {}
		
		dao.save(criteria);
		dao.find(criteria.getId());
		assertEquals(FlagColor.Red, criteria.getFlagColor());
	}
}
