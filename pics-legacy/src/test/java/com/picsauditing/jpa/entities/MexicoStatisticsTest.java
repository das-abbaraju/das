package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;

public class MexicoStatisticsTest {
	private MexicoStatistics stat;
	private List<AuditData> list;

	@Mock
	private AuditData auditData;
	

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		list = new ArrayList<AuditData>();

		stat = new MexicoStatistics(2012, list, true);
	}

	@Test
	public void testGetStats() {
		AuditQuestion trirQuestion = new AuditQuestion();
		AuditQuestion lwcrQuestion = new AuditQuestion();
		trirQuestion.setId(MexicoStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);
		lwcrQuestion.setId(MexicoStatistics.QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR);
		
		AuditData data;
		data = EntityFactory.makeAuditData("14,000", trirQuestion);
		list.add(data);
		data = EntityFactory.makeAuditData("14000", lwcrQuestion);
		list.add(data);
		
		stat = new MexicoStatistics(2011, list, true);
		
		assertEquals(stat.getStats(OshaRateType.TrirAbsolute), "14000");
		assertEquals(stat.getStats(OshaRateType.LwcrAbsolute), "14000");
	}

	@Test
	public void testEmptyGetStats() {
		AuditQuestion question = new AuditQuestion();
		question.setId(MexicoStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);

		AuditData data;
		data = EntityFactory.makeAuditData("", question);
		list.add(data);

		stat = new MexicoStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.TrirAbsolute), "0");

		data.setAnswer(null);
		stat = new MexicoStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.TrirAbsolute), "0");
	}

}
