package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.picsauditing.EntityFactory;

public class OshaStatisticsTest {
	OshaStatistics stat;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetStats() {
		List<AuditData> list = new ArrayList<AuditData>();
		
		AuditQuestion trirQuestion = new AuditQuestion();
		AuditQuestion lwcrQuestion = new AuditQuestion();
		trirQuestion.setId(OshaStatistics.QUESTION_ID_TRIR_FOR_THE_GIVEN_YEAR);
		lwcrQuestion.setId(OshaStatistics.QUESTION_ID_LWCR_FOR_THE_GIVEN_YEAR);
		
		AuditData data;
		data = EntityFactory.makeAuditData("14,000", trirQuestion);
		list.add(data);
		data = EntityFactory.makeAuditData("14000", lwcrQuestion);
		list.add(data);
		
		stat = new OshaStatistics(2011, list, true);
		
		assertEquals(stat.getStats(OshaRateType.TrirAbsolute), "14000");
		assertEquals(stat.getStats(OshaRateType.LwcrAbsolute), "14000");
	}
	}

