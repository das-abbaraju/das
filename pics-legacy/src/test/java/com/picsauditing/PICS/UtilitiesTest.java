package com.picsauditing.PICS;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.model.i18n.KeyValue;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class UtilitiesTest {

	@Test
	public void testIsEmptyArray() {
		assertTrue(Utilities.isEmptyArray(null));
	}

	@Test
	public void testEscapeHTML() throws Exception {
		assertEquals("", Utilities.escapeHTML(""));
		assertEquals("hello", Utilities.escapeHTML("hello"));
		assertEquals("&#60;hello&#62;", Utilities.escapeHTML("<hello>"));
		assertEquals("&#34;hello&#34;", Utilities.escapeHTML("\"hello\""));
		assertEquals("&#39;hello&#39;", Utilities.escapeHTML("'hello'"));
		assertEquals("hello<br/>goodbye", Utilities.escapeHTML("hello\ngoodbye"));
	}

	@Test
	public void testEscapeHTML_NotTruncated_NoExpansion() throws Exception {
		String plainFox = Utilities.escapeHTML("The quick brown fox", 50);
		assertEquals("The quick brown fox", plainFox);
		assertEquals(19, plainFox.length());
	}

	@Test
	public void testEscapeHTML_Truncated_NoExpansion() throws Exception {
		String plainFox = Utilities.escapeHTML("The quick brown fox jumped over the lazy dog.", 19);
		assertEquals("The quick brown fox...", plainFox);
		assertEquals(22, plainFox.length());
	}

	@Test
	public void testEscapeHTML_Truncated_Expanded() throws Exception {
		// The string is truncated before the special symbols are expanded
		String quotedFox = Utilities.escapeHTML("The 'quick' brown fox jumped over the lazy dog.", 21);
		assertEquals("The &#39;quick&#39; brown fox...", quotedFox);
		assertEquals(32, quotedFox.length());
	}

	@Test
	public void testEscapeHTML_NotTruncated_Expanded() throws Exception {
		String quotedFox = Utilities.escapeHTML("The 'quick' brown fox", 50);
		assertEquals("The &#39;quick&#39; brown fox", quotedFox);
		assertEquals(29, quotedFox.length());
	}

	@Test
	public void testCollectionsAreEqual_NullOrEmptyCollections() {
		Collection<String> emptyStringCollection = null;
		assertFalse(Utilities.collectionsAreEqual(emptyStringCollection, emptyStringCollection, String.CASE_INSENSITIVE_ORDER));

		emptyStringCollection = new ArrayList<String>();
		assertFalse(Utilities.collectionsAreEqual(emptyStringCollection, emptyStringCollection, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_DifferentSizedCollections() {
		Collection<String> collection1 = Arrays.asList("AB", "AC");
		Collection<String> collection2 = Arrays.asList("AB");
		assertFalse(Utilities.collectionsAreEqual(collection1, collection2, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_CollectionsDoNotHaveSameContents() {
		Collection<String> collection1 = Arrays.asList("AB", "AC");
		Collection<String> collection2 = Arrays.asList("AB", "CA");
		assertFalse(Utilities.collectionsAreEqual(collection1, collection2, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_CollectionsHaveSameContents() {
		Collection<String> collection1 = Arrays.asList("DE", "AB", "AC");
		Collection<String> collection2 = Arrays.asList("AC", "DE", "AB");
		assertTrue(Utilities.collectionsAreEqual(collection1, collection2, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_CollectionsHaveSameContents_EntriesImplement_Comparable() {
		Collection<Integer> collection1 = Arrays.asList(1, 2, 3);
		Collection<Integer> collection2 = Arrays.asList(3, 2, 1);
		assertTrue(Utilities.collectionsAreEqual(collection1, collection2));
	}

	@Test
	public void testGetIdsBaseTableEntities() {
		List<AuditType> audits = Arrays.asList(EntityFactory.makeAuditType(1),
				EntityFactory.makeAuditType(2), EntityFactory.makeAuditType(3));

		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(1, 2, 3), Utilities.getIdsBaseTableEntities(audits)));
	}

	@Test
	public void testPrimitiveArrayToList() {
		List<Integer> result = Utilities.primitiveArrayToList(new int[]{1, 2, 3});

		assertEquals(3, result.size());
		assertTrue(Utilities.collectionsAreEqual(new ArrayList(Arrays.asList(1, 2, 3)), result));
	}

	@Test
	public void testPrimitiveArrayToList_NullArray() {
		assertTrue(Utilities.primitiveArrayToList(null).isEmpty());
	}

	@Test
	public void testMapsAreEquals_BothMapsAreNull() {
		boolean result = Utilities.mapsAreEqual(null, null);

		assertTrue(result);
	}

	@Test
	public void testMapsAreEquals_OneMapIsNullOtherIsNot() {
		boolean result = Utilities.mapsAreEqual(null, new HashMap());

		assertFalse(result);

		result = Utilities.mapsAreEqual(new HashMap(), null);

		assertFalse(result);
	}

	@Test
	public void testMapsAreEquals_NotTheSameSize() {
		Map map1 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("C", "D"));
		Map map2 = buildMap(new KeyValue<>("A", "B"));

		boolean result = Utilities.mapsAreEqual(map1, map2);

		assertFalse(result);
	}

	@Test
	public void testMapsAreEquals_DifferentMapValue() {
		Map map1 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("C", "D"));
		Map map2 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("C", "E"));

		boolean result = Utilities.mapsAreEqual(map1, map2);

		assertFalse(result);
	}

	@Test
	public void testMapsAreEquals_DifferentMapKey() {
		Map map1 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("C", "D"));
		Map map2 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("F", "D"));

		boolean result = Utilities.mapsAreEqual(map1, map2);

		assertFalse(result);
	}

	@Test
	public void testMapsAreEquals() {
		Map map1 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("C", "D"));
		Map map2 = buildMap(new KeyValue<>("A", "B"), new KeyValue<>("C", "D"));

		boolean result = Utilities.mapsAreEqual(map1, map2);

		assertTrue(result);
	}

	private Map<String, String> buildMap(KeyValue<String, String>... keyValues) {
		Map<String, String> map = new HashMap<>();
		for (KeyValue<String, String> keyValue : keyValues) {
			map.put(keyValue.getKey(), keyValue.getValue());
		}

		return map;
	}

    private static final Utilities.MapConvertable<Integer, SimpleObject> SIMPLE_OBJECTMAP_CONVERTABLE = new Utilities.MapConvertable<Integer, SimpleObject>() {

        @Override
        public Integer getKey(SimpleObject entity) {
            return entity.getId();
        }
    };

    @Test
    public void testCoalesceIntoMap_EmptyCollection() {
        Map<Integer, SimpleObject> result = Utilities.convertToMap(null, SIMPLE_OBJECTMAP_CONVERTABLE);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCoalesceIntoMap() {
        Map<Integer, SimpleObject> result = Utilities.convertToMap(getSimpleObjects(), SIMPLE_OBJECTMAP_CONVERTABLE);

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
        Map<Integer, List<SimpleObject>> result = Utilities.convertToMapOfLists(null, SIMPLE_OBJECTMAP_CONVERTABLE);

        assertTrue(result.isEmpty());
    }

    @Test
    public void testCoalesceIntoMapOfLists() {
        Map<Integer, List<SimpleObject>> result = Utilities.convertToMapOfLists(getSimpleObjects(), SIMPLE_OBJECTMAP_CONVERTABLE);

        verifyCoalesceIntoMapOfLists(result);
    }

	@Test
	public void testInvertMapOfList() {
		String key1 = "Test 1";
		String key2 = "Test 2";

		Map<String, List<Integer>> map = new HashMap<>();
		map.put(key1, Arrays.asList(1, 2));
		map.put(key2, Arrays.asList(2, 3));

		Map<Integer, List<String>> invertedMap = Utilities.invertMapOfList(map);

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

		Map<Integer, String> invertedMap = Utilities.invertMap(map);

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
		Set<String> results = Utilities.flattenCollectionOfCollection(listOfSets);

		assertTrue(results.contains(string1));
		assertTrue(results.contains(string2));
		assertTrue(results.contains(string3));
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
}
