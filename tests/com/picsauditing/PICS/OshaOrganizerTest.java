package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.MultiYearScope;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaRateType;
import com.picsauditing.jpa.entities.OshaType;

public class OshaOrganizerTest extends TestCase {
	private ContractorAccount contractor = EntityFactory.makeContractor();
	private List<ContractorAudit> list = new ArrayList<ContractorAudit>();

	public void testOsha() {
		list.clear();
		addOsha(2009, 200000, 1);
		{
			OshaOrganizer organizer = new OshaOrganizer(list);
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
			assertEquals(-1f, organizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute));
			assertEquals(-1f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearsAgo, OshaRateType.TrirAbsolute));
			assertEquals(-1f, organizer.getRate(OshaType.COHS, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute));
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearWeightedAverage, OshaRateType.TrirAbsolute));
		}
		addOsha(2008, 400000, 1);
		{
			OshaOrganizer organizer = new OshaOrganizer(list);
			assertEquals(1f, organizer.getRate(OshaType.OSHA, MultiYearScope.LastYearOnly, OshaRateType.TrirAbsolute));
			assertEquals(0.5f, organizer.getRate(OshaType.OSHA, MultiYearScope.TwoYearsAgo, OshaRateType.TrirAbsolute));
			assertEquals(0.75f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearAverage, OshaRateType.TrirAbsolute));
			assertEquals(0.6666667f, organizer.getRate(OshaType.OSHA, MultiYearScope.ThreeYearWeightedAverage, OshaRateType.TrirAbsolute));
		}
	}
	
	private ContractorAudit addAudit(String year) {
		ContractorAudit audit = EntityFactory.makeContractorAudit(11, contractor);
		audit.setAuditFor(year);
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
		return osha;
	}
}
