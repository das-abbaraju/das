package com.picsauditing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;


public class CorruptionPerceptionIndexMapTest {

	CorruptionPerceptionIndexMap corruptionPerceptionIndexMap;
	
	@Before
	public void setup() {
		corruptionPerceptionIndexMap = new CorruptionPerceptionIndexMap();
		
		Map<String, Double> map = new HashMap<String, Double>();
		map.put("US", 7.1);
		map.put("CA", 8.7);
		map.put("NZ", 9.5);
		map.put("IR", 2.7);
		map.put("LK", 3.0);
		
		// Not all countries are tracked by transparancy.org, for example...
		map.put("RE", null);
		map.put("CX", null);
		
		Whitebox.setInternalState(corruptionPerceptionIndexMap, "map", map);
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
