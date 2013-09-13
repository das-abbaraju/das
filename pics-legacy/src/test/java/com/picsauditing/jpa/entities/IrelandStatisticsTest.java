package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;

public class IrelandStatisticsTest {
	private IrelandStatistics stat;
	private List<AuditData> list;

	@Mock
	private AuditData auditData;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		list = new ArrayList<AuditData>();

		stat = new IrelandStatistics(2012, list, true);
	}

	@Test
	public void testGetStats() {
		AuditQuestion afrQuestion = new AuditQuestion();
		AuditQuestion irQuestion = new AuditQuestion();
		afrQuestion.setId(IrelandStatistics.QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR);
		irQuestion.setId(IrelandStatistics.QUESTION_ID_IR_FOR_THE_GIVEN_YEAR);

		AuditData data;
		data = EntityFactory.makeAuditData("14,000", afrQuestion);
		list.add(data);
		data = EntityFactory.makeAuditData("14000", irQuestion);
		list.add(data);

		stat = new IrelandStatistics(2011, list, true);

		assertEquals(stat.getStats(OshaRateType.IFR), "14000");
		assertEquals(stat.getStats(OshaRateType.IR), "14000");
	}

	@Test
	public void testEmptyGetStats() {
		AuditQuestion question = new AuditQuestion();
		question.setId(IrelandStatistics.QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR);

		AuditData data;
		data = EntityFactory.makeAuditData("", question);
		list.add(data);

		stat = new IrelandStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.IFR), "0");

		data.setAnswer(null);
		stat = new IrelandStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.IFR), "0");
	}

}
