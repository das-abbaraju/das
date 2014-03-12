package com.picsauditing.employeeguard.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class PicsCollectionUtilTest {

	@Test
	public void testConvertToMapOfMaps_EmptyNullValues() {
		Map<Integer, Map<String, TestObject>> result = PicsCollectionUtil.convertToMapOfMaps(null, new TestObjectConverter());

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testConvertToMapOfMaps_NullConverter() {
		Map<Integer, Map<String, TestObject>> result = PicsCollectionUtil.convertToMapOfMaps(
				Arrays.asList(new TestObject(1, "Test")),
				null);

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testConvertToMapOfMaps_WithValues() {
		TestObject testObject1 = new TestObject(1, "Test 1");
		TestObject testObject2 = new TestObject(2, "Test 2");
		TestObject testObject3 = new TestObject(3, "Test 3");

		Map<Integer, Map<String, TestObject>> result = PicsCollectionUtil.convertToMapOfMaps(
				Arrays.asList(testObject1, testObject2, testObject3),
				new TestObjectConverter());

		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(result.containsKey(1));
		assertTrue(result.get(1).containsKey("Test 1"));
		assertEquals(testObject1, result.get(1).get("Test 1"));
		assertEquals(3, result.size());
	}

	private class TestObject {
		private int time;
		private String name;

		public TestObject(int time, String name) {
			this.time = time;
			this.name = name;
		}

		public int getTime() {
			return time;
		}

		public void setTime(int time) {
			this.time = time;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private class TestObjectConverter implements PicsCollectionUtil.CollectionToMapConverter<Integer, String, TestObject> {
		@Override
		public Integer getRow(TestObject value) {
			return value.getTime();
		}

		@Override
		public String getColumn(TestObject value) {
			return value.getName();
		}
	}

}
