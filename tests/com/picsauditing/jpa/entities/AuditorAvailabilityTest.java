package com.picsauditing.jpa.entities;

import junit.framework.TestCase;

import org.junit.Test;

public class AuditorAvailabilityTest extends TestCase {
	
	@Test
	public void testSerialize() {
		AuditorAvailability availability = new AuditorAvailability();
		AvailabilityRestrictions restrictions = availability.getRestrictionsObject();
		
		restrictions.setWebOnly(true);
		restrictions.setOnlyInStates(new String[]{"TX","LA","OK"});
		restrictions.setNearLatitude(12.345f);
		restrictions.setNearLongitude(54.321f);
		
		availability.setRestrictionsObject(restrictions);
		AvailabilityRestrictions restriction2 = availability.getRestrictionsObject();

		assertEquals(restriction2.isWebOnly(), restrictions.isWebOnly());
		assertEquals(restriction2.getNearLatitude(), restrictions.getNearLatitude());
		assertEquals(restriction2.getNearLongitude(), restrictions.getNearLongitude());
		assertEquals(restriction2.getOnlyInStates().length, restrictions.getOnlyInStates().length);
	}

}
