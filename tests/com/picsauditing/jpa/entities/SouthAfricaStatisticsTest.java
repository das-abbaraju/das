package com.picsauditing.jpa.entities;

import com.picsauditing.EntityFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SouthAfricaStatisticsTest {
	private SouthAfricaStatistics stat;
	private List<AuditData> list;

	@Mock
	private AuditData auditData;


	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		list = new ArrayList<>();

		stat = new SouthAfricaStatistics(2012, list, true);
	}

	@Test
	public void testGetStats() {
		AuditQuestion afr = new AuditQuestion();
		AuditQuestion fatalities = new AuditQuestion();
		afr.setId(SouthAfricaStatistics.QUESTION_ID_AFR_FOR_THE_GIVEN_YEAR);
		fatalities.setId(SouthAfricaStatistics.QUESTION_ID_FATALITIES_FOR_THE_GIVEN_YEAR);

		AuditData data;
		data = EntityFactory.makeAuditData("14,000", afr);
		list.add(data);
		data = EntityFactory.makeAuditData("14000", fatalities);
		list.add(data);

		stat = new SouthAfricaStatistics(2011, list, true);

		assertEquals(stat.getStats(OshaRateType.IFR), "14000");
		assertEquals(stat.getStats(OshaRateType.Fatalities), "14000");
	}

	@Test
	public void testEmptyGetStats() {
		AuditQuestion question = new AuditQuestion();
		question.setId(SouthAfricaStatistics.QUESTION_ID_IR_FOR_THE_GIVEN_YEAR);

		AuditData data;
		data = EntityFactory.makeAuditData("", question);
		list.add(data);

		stat = new SouthAfricaStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.IR), "0");

		data.setAnswer(null);
		stat = new SouthAfricaStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.IR), "0");
	}

}
