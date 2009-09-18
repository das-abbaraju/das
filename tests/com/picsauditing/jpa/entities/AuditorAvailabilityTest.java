package com.picsauditing.jpa.entities;

import junit.framework.TestCase;

import org.junit.Test;

import com.picsauditing.util.Location;

public class AuditorAvailabilityTest extends TestCase {

	@Test
	public void testSerialize() {
		AuditorAvailability availability = new AuditorAvailability();
		AvailabilityRestrictions restrictions = availability.getRestrictionsObject();

		restrictions.setWebOnly(true);
		restrictions.setOnlyInStates(new String[] { "TX", "LA", "OK" });
		restrictions.setLocation(new Location(12.345f, 54.321f));

		availability.setRestrictionsObject(restrictions);
		AvailabilityRestrictions restriction2 = availability.getRestrictionsObject();

		assertEquals(restriction2.isWebOnly(), restrictions.isWebOnly());
		assertEquals(restriction2.getLocation().getLatitude(), restrictions.getLocation().getLatitude());
		assertEquals(restriction2.getLocation().getLongitude(), restrictions.getLocation().getLongitude());
		assertEquals(restriction2.getOnlyInStates().length, restrictions.getOnlyInStates().length);
	}

}
