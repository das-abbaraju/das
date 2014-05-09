package com.picsauditing.employeeguard.util;

import com.picsauditing.PICS.Utilities;
import org.junit.Test;

import java.util.*;

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

	private static final PicsCollectionUtil.MapConvertable<Integer, SimpleObject> SIMPLE_OBJECTMAP_CONVERTABLE = new PicsCollectionUtil.MapConvertable<Integer, SimpleObject>() {

		@Override
		public Integer getKey(SimpleObject entity) {
			return entity.getId();
		}
	};

	@Test
	public void testCoalesceIntoMap_EmptyCollection() {
		Map<Integer, SimpleObject> result = PicsCollectionUtil.convertToMap(null, SIMPLE_OBJECTMAP_CONVERTABLE);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testCoalesceIntoMap() {
		Map<Integer, SimpleObject> result = PicsCollectionUtil.convertToMap(getSimpleObjects(), SIMPLE_OBJECTMAP_CONVERTABLE);

		verifyCoalesceIntoMapResult(result);
	}

	private void verifyCoalesceIntoMapResult(Map<Integer, SimpleObject> result) {
		Map<Integer, SimpleObject> correctResult = new HashMap<Integer, SimpleObject>() {{
			put(1, new SimpleObject(1, "John"));
			put(2, new SimpleObject(2, "Bob"));
			put(3, new SimpleObject(3, "Mary"));
		}};

		assertTrue(Utilities.mapsAreEqual(correctResult, result));
	}

	@Test
	public void testCoalesceIntoMapOfLists_EmptyCollection() {
		Map<Integer, List<SimpleObject>> result = PicsCollectionUtil.convertToMapOfLists(null, SIMPLE_OBJECTMAP_CONVERTABLE);

		assertTrue(result.isEmpty());
	}

	@Test
	public void testCoalesceIntoMapOfLists() {
		Map<Integer, List<SimpleObject>> result = PicsCollectionUtil.convertToMapOfLists(getSimpleObjects(), SIMPLE_OBJECTMAP_CONVERTABLE);

		verifyCoalesceIntoMapOfLists(result);
	}

	@Test
	public void testInvertMapOfList() {
		String key1 = "Test 1";
		String key2 = "Test 2";

		Map<String, List<Integer>> map = new HashMap<>();
		map.put(key1, Arrays.asList(1, 2));
		map.put(key2, Arrays.asList(2, 3));

		Map<Integer, List<String>> invertedMap = PicsCollectionUtil.invertMapOfList(map);

		assertTrue(invertedMap.get(1).contains(key1));
		assertTrue(invertedMap.get(2).contains(key1));
		assertFalse(invertedMap.get(3).contains(key1));

		assertTrue(invertedMap.get(2).contains(key2));
		assertTrue(invertedMap.get(3).contains(key2));
		assertFalse(invertedMap.get(1).contains(key2));
	}

	@Test
	public void testInvertMap() {
		String key1 = "Test 10";
		String key2 = "Test 20";

		Map<String, Integer> map = new HashMap<>();
		map.put(key1, 10);
		map.put(key2, 20);

		Map<Integer, String> invertedMap = PicsCollectionUtil.invertMap(map);

		assertNotNull(invertedMap.get(10));
		assertEquals(key1, invertedMap.get(10));
		assertNotNull(invertedMap.get(20));
		assertEquals(key2, invertedMap.get(20));
	}

	@Test
	public void testFlattenCollectionOfCollection() {
		String string1 = "Test 1";
		String string2 = "Test 2";
		String string3 = "Test 3";

		Set<String> set1 = new HashSet<>(Arrays.asList(string1, string2));
		Set<String> set2 = new HashSet<>(Arrays.asList(string3));

		List<Set<String>> listOfSets = new ArrayList<>(Arrays.asList(set1, set2));
		Set<String> results = PicsCollectionUtil.flattenCollectionOfCollection(listOfSets);

		assertTrue(results.contains(string1));
		assertTrue(results.contains(string2));
		assertTrue(results.contains(string3));
	}

	@Test
	public void testMergeValuesOfMapOfSets_EmptyMap() {
		Map<Integer, Set<String>> map1 = Collections.emptyMap();

		Map<Integer, Set<String>> map2 = new HashMap<>();
		map2.put(2, new HashSet<>(Arrays.asList("Test 4")));
		map2.put(3, new HashSet<>(Arrays.asList("Test 5")));

		Map<Integer, Set<String>> result = PicsCollectionUtil.mergeMapOfSets(map1, map2);

		assertEquals(2, result.size());
		assertTrue(Utilities.mapsAreEqual(map2, result));
	}

	@Test
	public void testMergeValuesOfMapOfSets() {
		Map<Integer, Set<String>> map1 = new HashMap<>();
		map1.put(1, new HashSet<>(Arrays.asList("Test 1", "Test 2")));
		map1.put(2, new HashSet<>(Arrays.asList("Test 3")));

		Map<Integer, Set<String>> map2 = new HashMap<>();
		map2.put(2, new HashSet<>(Arrays.asList("Test 4")));
		map2.put(3, new HashSet<>(Arrays.asList("Test 5")));

		Map<Integer, Set<String>> mergedValues = PicsCollectionUtil.mergeMapOfSets(map1, map2);

		performAssertionsOnTestMergeValuesOfMapOfSets(mergedValues);
	}

	private void performAssertionsOnTestMergeValuesOfMapOfSets(Map<Integer, Set<String>> mergedValues) {
		assertNotNull(mergedValues);
		assertFalse(mergedValues.isEmpty());
		assertTrue(mergedValues.containsKey(1));
		assertTrue(mergedValues.containsKey(2));
		assertTrue(mergedValues.containsKey(3));
		assertEquals(2, mergedValues.get(1).size());
		assertEquals(2, mergedValues.get(2).size());
		assertEquals(1, mergedValues.get(3).size());
		assertTrue(mergedValues.get(1).contains("Test 1"));
		assertTrue(mergedValues.get(1).contains("Test 2"));
		assertTrue(mergedValues.get(2).contains("Test 3"));
		assertTrue(mergedValues.get(2).contains("Test 4"));
		assertTrue(mergedValues.get(3).contains("Test 5"));
	}

	private void verifyCoalesceIntoMapOfLists(Map<Integer, List<SimpleObject>> result) {
		Map<Integer, List<SimpleObject>> correctResult = new HashMap<Integer, List<SimpleObject>>() {{
			put(1, Arrays.asList(new SimpleObject(1, "John")));
			put(2, Arrays.asList(new SimpleObject(2, "Allen"), new SimpleObject(2, "Bob")));
			put(3, Arrays.asList(new SimpleObject(3, "Mary")));
		}};

		assertTrue(Utilities.mapsAreEqual(correctResult, result));
	}

	private List<SimpleObject> getSimpleObjects() {
		List<SimpleObject> simpleObjects = new ArrayList<>();
		simpleObjects.add(new SimpleObject(1, "John"));
		simpleObjects.add(new SimpleObject(2, "Allen"));
		simpleObjects.add(new SimpleObject(2, "Bob"));
		simpleObjects.add(new SimpleObject(3, "Mary"));
		return simpleObjects;
	}

	private class SimpleObject {

		private int id;
		private String name;

		public SimpleObject(int id, String name) {
			this.id = id;
			this.name = name;
		}

		private int getId() {
			return id;
		}

		private String getName() {
			return name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			SimpleObject that = (SimpleObject) o;

			if (id != that.id) return false;
			if (name != null ? !name.equals(that.name) : that.name != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = id;
			result = 31 * result + (name != null ? name.hashCode() : 0);
			return result;
		}
	}

	@Test
	public void testAddKeys() {
		Map<Integer, Set<Integer>> fakeMap = buildFakeMap();
		List<Integer> fakeKeys = buildFakeKeys();

		Map<Integer, Set<Integer>> result = PicsCollectionUtil.addKeys(fakeMap, fakeKeys);

		verifyTestAddKeys(result);
	}

	private Map<Integer, Set<Integer>> buildFakeMap() {
		return new HashMap<Integer, Set<Integer>>() {{

			put(1, new HashSet<>(Arrays.asList(2)));
			put(2, new HashSet<>(Arrays.asList(3)));
			put(3, new HashSet<>(Arrays.asList(4)));

		}};
	}

	private List<Integer> buildFakeKeys() {
		return new ArrayList<>(Arrays.asList(0, 1, 2, 4, 3, 4));
	}

	private void verifyTestAddKeys(Map<Integer, Set<Integer>> result) {
		Utilities.collectionsAreEqual(Arrays.asList(0, 1, 2, 3, 4), result.keySet());
		Utilities.collectionsAreEqual(Arrays.asList(2), result.get(1));
		Utilities.collectionsAreEqual(Arrays.asList(3), result.get(2));
		Utilities.collectionsAreEqual(Arrays.asList(4), result.get(3));
	}

}
