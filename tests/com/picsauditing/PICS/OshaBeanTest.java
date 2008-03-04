package com.picsauditing.PICS;

import com.picsauditing.PICS.OSHABean;

import junit.framework.TestCase;

public class OshaBeanTest extends TestCase {
	private OSHABean oBean;

	public OshaBeanTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		String oshaID = "2505";
		oBean = new OSHABean();
		oBean.setConn(DefaultDatabase.getConnection());
		oBean.setFromDB(oshaID);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testManHours1() {
		String stat = oBean.getStat(OSHABean.MAN_HOURS, OSHABean.YEAR1);
		assertEquals("26,968.00", stat);
		stat = oBean.calcAverageStat(OSHABean.MAN_HOURS);
		assertEquals("15,560.00", stat);
    }

	public void testFatalities2() {
		String stat = oBean.getStat(OSHABean.FATALITIES, OSHABean.YEAR2);
		assertEquals("0.00", stat);
		stat = oBean.calcAverageStat(OSHABean.FATALITIES);
		assertEquals("0.00", stat);
    }

	public void testLostWorkCases2() {
		String stat = oBean.getStat(OSHABean.LOST_WORK_CASES, OSHABean.YEAR2);
		assertEquals("0.00", stat);
		stat = oBean.calcAverageStat(OSHABean.LOST_WORK_CASES);
		assertEquals("0.00", stat);
    }
}
