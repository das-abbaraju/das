package com.picsauditing.jpa.entities;

import com.picsauditing.EntityFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UkStatisticsTest {
	private List<AuditData> list;
	private UkStatistics stat;

	@Before
	public void setUp() throws Exception {
		list = new ArrayList<AuditData>();
	}

	@Test
	public void testGetOshaType() {
		UkStatistics stats = new UkStatistics(2012, generateTestData1(), true);
		assertEquals(OshaType.UK_HSE, stats.getOshaType());
	}

	@Test
	public void testGetStats() {
		UkStatistics stats = new UkStatistics(2012, generateTestData1(), true);
		assertEquals("1", stats.getStats(OshaRateType.IFR));
		assertEquals("2", stats.getStats(OshaRateType.Hours));
		assertEquals("3", stats.getStats(OshaRateType.Fatalities));
		assertEquals("4", stats.getStats(OshaRateType.DOFR));
		assertEquals("5", stats.getStats(OshaRateType.LTIFR));
		assertEquals("6", stats.getStats(OshaRateType.FileUpload));
	}

	private List<AuditData> generateTestData1() {
		List<AuditData> statsAnswers = new ArrayList<AuditData>();

		statsAnswers.add(EntityFactory.makeAuditData("1", 9060));
		statsAnswers.add(EntityFactory.makeAuditData("2", 9099));
		statsAnswers.add(EntityFactory.makeAuditData("3", 8867));
		statsAnswers.add(EntityFactory.makeAuditData("4", 9062));
		statsAnswers.add(EntityFactory.makeAuditData("5", 11689));
		statsAnswers.add(EntityFactory.makeAuditData("6", 8873));

		return statsAnswers;
		
	}

	@Test
	public void testEmptyGetStats() {
		AuditQuestion question = new AuditQuestion();
		question.setId(9060);

		AuditData data;
		data = EntityFactory.makeAuditData("", question);
		list.add(data);

		stat = new UkStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.IFR), "0");

		data.setAnswer(null);
		stat = new UkStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.IFR), "0");
	}
}
