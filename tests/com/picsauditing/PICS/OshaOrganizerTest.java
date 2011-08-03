package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;

public class OshaOrganizerTest extends TestCase {
	private ContractorAccount contractor = EntityFactory.makeContractor();
	private List<ContractorAudit> list = new ArrayList<ContractorAudit>();

	public void testOsha() {
		list.clear();
		int currentYear = DateBean.getCurrentYear();

		addOsha(currentYear - 1, 200000, 1);
		{
			OshaOrganizer organizer = new OshaOrganizer(list);
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
			assertNull(organizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute));
			assertNull(organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearsAgo, OshaRateType.TrirAbsolute));
			assertNull(organizer.getRate(OshaType.COHS, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute));
		}
		addOsha(currentYear - 2, 400000, 1);
		{
			OshaOrganizer organizer = new OshaOrganizer(list);
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
			assertEquals(0.5f, organizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute));
			assertEquals(0.75f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute));
		}
	}
	
	private ContractorAudit addAudit(String year) {
		ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
		audit.setAuditFor(year);
		EntityFactory.addCao(audit, EntityFactory.makeOperator()).changeStatus(AuditStatus.Complete, null);
		
		list.add(audit);
		return audit;
	}

	private OshaAudit addOsha(int year, int manHours, int recordables) {
		ContractorAudit audit = addAudit(Integer.toString(year));
		OshaAudit osha = new OshaAudit();
		audit.getOshas().add(osha);
		osha.setConAudit(audit);
		osha.setCorporate(true);
		osha.setManHours(manHours);
		osha.setRecordableTotal(recordables);
		
		AuditData answer = new AuditData();
		answer.setAudit(audit);
		answer.setQuestion(EntityFactory.makeAuditQuestion());
		answer.getQuestion().setId(2064);
		answer.setAnswer("Yes");
		audit.getData().add(answer);
		
		return osha;
	}
}
