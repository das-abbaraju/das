package com.picsauditing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class CorruptionPerceptionIndexMapTest {

	CorruptionPerceptionIndexMap corruptionPerceptionIndexMap;
	
	@Before
	public void setup() {
		corruptionPerceptionIndexMap = new CorruptionPerceptionIndexMap();
		corruptionPerceptionIndexMap.map = new HashMap<String, Double>();
		corruptionPerceptionIndexMap.map.put("US", 7.1);
		corruptionPerceptionIndexMap.map.put("CA", 8.7);
		corruptionPerceptionIndexMap.map.put("NZ", 9.5);
		corruptionPerceptionIndexMap.map.put("IR", 2.7);
		corruptionPerceptionIndexMap.map.put("LK", 3.0);
		
		// Not all countries are tracked by transparancy.org, for example...
		corruptionPerceptionIndexMap.map.put("RE", null);
		corruptionPerceptionIndexMap.map.put("CX", null);
	}
	
	@Test
	public void testFindCorruptionPerceptionIndex() {
		assertEquals(new Double(7.1), corruptionPerceptionIndexMap.findCorruptionPerceptionIndex("US"));
		assertEquals(new Double(8.7), corruptionPerceptionIndexMap.findCorruptionPerceptionIndex("CA"));
		assertEquals(new Double(3.0), corruptionPerceptionIndexMap.findCorruptionPerceptionIndex("LK"));
	}
	
	@Test
	public void testFindCountryWithNullCorruptionPerceptionIndex() {
		assertNull(corruptionPerceptionIndexMap.findCorruptionPerceptionIndex("RE"));
		assertNull(corruptionPerceptionIndexMap.findCorruptionPerceptionIndex("CX"));
	}
	@Test
	public void testFindCorruptionPerceptionIndices() {
		String json = "[{\"id\":\"US\"},{\"id\":\"CA\"},{\"id\":\"CX\"}]";
		List<Double> cpis = corruptionPerceptionIndexMap.findCorruptionPerceptionIndices(json);
		assertEquals(7.1,cpis.get(0),0.05);
		assertEquals(8.7,cpis.get(1),0.05);
		assertNull(cpis.get(2));
	}
}
