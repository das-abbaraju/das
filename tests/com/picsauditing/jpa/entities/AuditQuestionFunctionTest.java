package com.picsauditing.jpa.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Multimap;
import com.picsauditing.PicsDBTest;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.util.AnswerMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class AuditQuestionFunctionTest extends PicsDBTest {
	@Autowired
	AuditQuestionDAO dao;
	@Autowired
	AuditDataDAO dataDao;

	@Test
	public void testFunction() throws Exception {
		AuditQuestion question = dao.find(7823);

		Collection<Integer> ids = new HashSet<Integer>();

		for (AuditQuestionFunction function : question.getFunctions()) {
			for (AuditQuestionFunctionWatcher watcher : function.getWatchers()) {
				ids.add(watcher.getQuestion().getId());
			}
		}

		AnswerMap answerMap = dataDao.findAnswers(332959, ids);

		Multimap<AuditQuestion, Object> runFunctions = question.runFunctions(QuestionFunctionType.Calculation,
				answerMap);

		for (Entry<AuditQuestion, Object> entry : runFunctions.entries()) {
			System.out.println("QUESTION: " + entry.getKey());
			System.out.println("VALUE   : " + entry.getValue());
		}
	}
}
