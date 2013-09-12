package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.picsauditing.EntityFactory;

public class AustraliaStatisticsTest {
    private AustraliaStatistics stat;
    private List<AuditData> list;

    @Mock
    private AuditData auditData;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        list = new ArrayList<AuditData>();

        stat = new AustraliaStatistics(2012, list, true);
    }

    @Test
    public void testGetStats() {
        AuditQuestion atlrQuestion = new AuditQuestion();
        AuditQuestion ltfrQuestion = new AuditQuestion();
        atlrQuestion.setId(AustraliaStatistics.QUESTION_ID_ATLR_FOR_THE_GIVEN_YEAR);
        ltfrQuestion.setId(AustraliaStatistics.QUESTION_ID_LTFR_FOR_THE_GIVEN_YEAR);

        AuditData data;
        data = EntityFactory.makeAuditData("14,000", atlrQuestion);
        list.add(data);
        data = EntityFactory.makeAuditData("14000", ltfrQuestion);
        list.add(data);

        stat = new AustraliaStatistics(2011, list, true);

        assertEquals(stat.getStats(OshaRateType.ATLR), "14000");
        assertEquals(stat.getStats(OshaRateType.LTIFR), "14000");
    }

	@Test
	public void testEmptyGetStats() {
		AuditQuestion atlrQuestion = new AuditQuestion();
		atlrQuestion.setId(AustraliaStatistics.QUESTION_ID_ATLR_FOR_THE_GIVEN_YEAR);

		AuditData data;
		data = EntityFactory.makeAuditData("", atlrQuestion);
		list.add(data);

		stat = new AustraliaStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.ATLR), "0");

		data.setAnswer(null);
		stat = new AustraliaStatistics(2011, list, true);
		assertEquals(stat.getStats(OshaRateType.ATLR), "0");
	}

}
