package com.picsauditing.flagcalculator.entities;

import com.picsauditing.flagcalculator.EntityFactory;
import com.picsauditing.flagcalculator.PicsTestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class OshaStatisticsTest {
	private OshaStatistics stat;
	private List<AuditData> list;

	@Mock
	private AuditData auditData;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		list = new ArrayList<AuditData>();

		stat = new OshaStatistics(2012, list, true);
	}

    @Test
    public void testGetCommentAuditData_ShaNotKept() {
        setupShaCommentData(false);
        stat.getCommentAuditData();
        assertEquals(stat.getHoursWorked(), stat.getCommentAuditData());
    }

    @Test
    public void testGetCommentAuditData_ShaKept() {
        setupShaCommentData(true);
        stat.getCommentAuditData();
        assertEquals(stat.getFileUpload(), stat.getCommentAuditData());
    }

    private void setupShaCommentData(boolean shaKept) {
        stat.setShaKept(shaKept);
        PicsTestUtil.forceSetPrivateField(stat, "fileUpload", new AuditData());
        PicsTestUtil.forceSetPrivateField(stat, "hoursWorked", new AuditData());
    }

	@Test
	public void testGetStats() {
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

	@Test
	public void testGetStats_NullAuditData() throws Exception {
		HashMap<OshaRateType, AuditData> answerMap = stat.getAnswerMap();
		answerMap.put(OshaRateType.TrirAbsolute, null);

		String stats = stat.getStats(OshaRateType.TrirAbsolute);

		assertTrue(stats == null);
	}

	@Test
	public void testGetStats_NullAnswer() throws Exception {
		when(auditData.getAnswer()).thenReturn((String) null);
		HashMap<OshaRateType, AuditData> answerMap = stat.getAnswerMap();
		answerMap.put(OshaRateType.TrirAbsolute, auditData);

		String stats = stat.getStats(OshaRateType.TrirAbsolute);

		assertTrue(stats == null);
	}
}

