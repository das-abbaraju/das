package com.picsauditing.auditbuilder.util;


import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class CorruptionPerceptionIndexMapTest {

	CorruptionPerceptionIndexMap2 corruptionPerceptionIndexMap;
	
	@Before
	public void setup() {
		corruptionPerceptionIndexMap = new CorruptionPerceptionIndexMap2();
		
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
	public void testFindCorruptionPerceptionIndices() {
		String json = "[{\"id\":\"US\"},{\"id\":\"CA\"},{\"id\":\"CX\"}]";
		List<Double> cpis = corruptionPerceptionIndexMap.findCorruptionPerceptionIndices(json);
		assertEquals(7.1,cpis.get(0),0.05);
		assertEquals(8.7,cpis.get(1),0.05);
		assertNull(cpis.get(2));
	}
}