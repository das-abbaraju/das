package com.picsauditing.PICS;

import com.picsauditing.dao.NaicsDAO;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTrade;
import com.picsauditing.jpa.entities.Naics;
import com.picsauditing.util.SpringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jsoup.Jsoup;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * A set of generic Utilities. We should consider moving this into the Strings
 * class
 */
public class Utilities {

	public static boolean isEmptyArray(Object[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return true;
		}

		for (Object object : array) {
			if (object == null) {
				return true;
			}
		}

		return false;
	}

	public static String escapeHTML(String value) {
		return escapeHTML(value, Integer.MAX_VALUE);
	}

	public static String stripTags(String value) {
		return Jsoup.parse(value).body().text();
	}

	/**
	 * This variation of escapeHTML(text) accepts a second argument of
	 * maxLength. It truncates the text to the given length, before escaping it.
	 * This means that we can safely truncate the text without worrying about
	 * truncating in the middle of an escape sequence.
	 * <p/>
	 * Additionally, if the text is truncated, then "..." is appended in place
	 * of the truncted text.
	 */
	public static String escapeHTML(String value, int maxLength) {
		int maxIndex = Math.min(maxLength, value.length());
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < maxIndex; i++) {
			char c = value.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>' || c == '\'') {
				out.append("&#" + (int) c + ";");
			} else if (c == '\n') {
				out.append("<br/>");
			} else {
				out.append(c);
			}
		}
		if (maxLength < value.length()) {
			out.append("...");
		}
		return out.toString();
	}

	public static String escapeNewLines(String value) {
		if (value == null) {
			return "";
		}
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < value.length(); i++) {
			if ('\n' == value.charAt(i)) {
				temp.append("<br>");
			} else {
				temp.append(value.charAt(i));
			}
		}
		return temp.toString();
	}

	public static String getBGColor(int count) {
		if ((count % 2) == 0) {
			return " bgcolor=\"#FFFFFF\"";
		} else {
			return "";
		}
	}

	@Deprecated
	public static float getAverageEMR(String year1, String year2, String year3, String year4) {
		Float rateFloat = 0.0f;
		int count = 0;
		if (convertToFloat(year1) > 0) {
			rateFloat += convertToFloat(year1);
			count++;
		}
		if (convertToFloat(year2) > 0) {
			rateFloat += convertToFloat(year2);
			count++;
		}
		if (convertToFloat(year3) > 0) {
			rateFloat += convertToFloat(year3);
			count++;
		}
		if (count < 3 && convertToFloat(year4) > 0) {
			rateFloat += convertToFloat(year4);
			count++;
		}

		Float avgRateFloat = rateFloat / count;
		return (float) Math.round(1000 * avgRateFloat) / 1000;
	}

	@Deprecated
	public static float convertToFloat(String year1) {
		if (year1 == null) {
			return 0.0f;
		}
		return Float.valueOf(year1).floatValue();
	}

	/**
	 * Deprecated in favor of the DateBean
	 */
	@Deprecated
	public static Date getYesterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}

	// TODO: Remove this from the Utilities class. This is not a general
	// purpose utility.
	public static float getIndustryAverage(boolean lwcr, ContractorAccount contractor) {
		float answer = 0f;
		ContractorTrade trade = contractor.getTopTrade();

		if (trade == null) {
			return answer;
		}

		if (!lwcr) {
			answer = trade.getTrade().getNaicsTRIRI();
		} else {
			answer = trade.getTrade().getNaicsLWCRI();
		}
		return answer;
	}

	// TODO: Remove from the Utilities class. This is not a general purpose
	// Utility.
	public static float getDartIndustryAverage(Naics naics) {
		NaicsDAO naicsDAO = SpringUtils.getBean("NaicsDAO");
		return naicsDAO.getDartIndustryAverage(naics);
	}

	/**
	 * Only to be used with smaller collections. There will be a performance
	 * bottle neck when used on larger collections.
	 */
	public static <E> boolean collectionsAreEqual(Collection<E> collection1, Collection<E> collection2,
	                                              Comparator<E> comparator) {
		if (CollectionUtils.isEmpty(collection1) || CollectionUtils.isEmpty(collection2)) {
			return false;
		}

		if (collection1.size() != collection2.size()) {
			return false;
		}

		for (E object : collection1) {
			boolean foundMatch = false;
			for (E objectForComparison : collection2) {
				if (comparator.compare(object, objectForComparison) == 0) {
					foundMatch = true;
					break;
				}
			}

			if (!foundMatch) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Only to be used with smaller collections. There will be a performance
	 * bottle neck when used on larger collections.
	 */
	public static <E extends Comparable<E>> boolean collectionsAreEqual(Collection<E> collection1,
	                                                                    Collection<E> collection2) {
		Comparator<E> comparableComparator = new Comparator<E>() {

			@Override
			public int compare(E o1, E o2) {
				return o1.compareTo(o2);
			}

		};

		return collectionsAreEqual(collection1, collection2, comparableComparator);
	}

	public static <E extends BaseTable> Collection<Integer> getIdsBaseTableEntities(Collection<E> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyList();
		}

		Collection<Integer> ids = new ArrayList<Integer>();
		for (E entity : entities) {
			ids.add(entity.getId());
		}

		return ids;
	}

	public static <K, V> boolean mapsAreEqual(Map<K, V> map1, Map<K, V> map2) {
		if (map1 == map2) {
			return true;
		} else if ((map1 == null && map2 != null) || (map1 != null && map2 == null)) {
			return false;
		} else if (map1.size() != map2.size()) {
			return false;
		}

		for (K key : map1.keySet()) {
			if (!map2.containsKey(key)) {
				return false;
			} else if (!map1.get(key).equals(map2.get(key))) {
				return false;
			}
		}

		return true;
	}

	// from
	// http://stackoverflow.com/questions/2768054/how-to-get-the-first-non-null-value-in-java
	public static <T> T coalesce(T... items) {
		for (T i : items) {
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	public static <T> T coalesce(T a, T b) {
		return a != null ? a : b;
	}

	public static <T> T coalesce(T a, T b, T c) {
		return a != null ? a : coalesce(b, c);
	}

	public static <T> T coalesce(T a, T b, T c, T d) {
		return a != null ? a : coalesce(b, c, d);
	}

	public static <T> T coalesce(T a, T b, T c, T d, T e) {
		return a != null ? a : coalesce(b, c, d, e);
	}

	public static List<Integer> primitiveArrayToList(int[] array) {
		if (ArrayUtils.isEmpty(array)) {
			return Collections.emptyList();
		}

		ArrayList<Integer> result = new ArrayList<>();
		for (int item : array) {
			result.add(item);
		}

		return result;
	}

	public interface MapConvertable<K, E> {

		K getKey(E entity);

	}

	/**
	 * Takes any collection of objects and returns a map based on the Key retrieved from the MapConvertable<K, E>
	 * implementation.
	 *
	 * @param entities
	 * @param mapConverter
	 * @param <K>          Object that represents the Key and must correctly implement the equals() and hashcode()
	 * @param <E>          Object that is some element/entity within the collection
	 * @return
	 */
	public static <K, E> Map<K, E> convertToMap(Collection<E> entities, MapConvertable<K, E> mapConverter) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, E> map = new HashMap<>();
		for (E entity : entities) {
			map.put(mapConverter.getKey(entity), entity);
		}

		return map;
	}

	/**
	 * Takes any collection of objects and returns a map of Key<K> -> List<E> where the Key is the value returned
	 * from the implementation of MapConvertable<K, E>.
	 *
	 * @param entities
	 * @param mapConverter
	 * @param <K>          Object that represents the Key and must correctly implement the equals() and hashcode()
	 * @param <E>          Object that is some element/entity within the collection
	 * @return
	 */
	public static <K, E> Map<K, List<E>> convertToMapOfLists(Collection<E> entities, MapConvertable<K, E> mapConverter) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, List<E>> map = new HashMap<>();
		for (E entity : entities) {
			K key = mapConverter.getKey(entity);
			addToMapOfKeyToList(map, key, entity);
		}

		return map;
	}

    /**
     * Takes any collection of objects and returns a map of Key<K> -> List<E> where the Key is the value returned
     * from the implementation of MapConvertable<K, E>.
     *
     * @param entities
     * @param mapConverter
     * @param <K>          Object that represents the Key and must correctly implement the equals() and hashcode()
     * @param <E>          Object that is some element/entity within the collection
     * @return
     */
    public static <K, E> Map<K, Set<E>> convertToMapOfSets(Collection<E> entities, MapConvertable<K, E> mapConverter) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyMap();
        }

        Map<K, Set<E>> map = new HashMap<>();
        for (E entity : entities) {
            K key = mapConverter.getKey(entity);
            addToMapOfKeyToSet(map, key, entity);
        }

        return map;
    }

	public interface Identitifable<E, ID> {

		ID getId(E element);

	}

	public static <E, ID> Set<ID> getIdsFromCollection(Collection<E> elements, Identitifable<E, ID> identitifable) {
		if (CollectionUtils.isEmpty(elements)) {
			return Collections.emptySet();
		}

		Set<ID> ids = new HashSet<>();
		for (E element : elements) {
			ids.add(identitifable.getId(element));
		}

		return ids;
	}

	public interface EntityKeyValueConvertable<E, K, V> {

		K getKey(E entity);

		V getValue(E entity);
	}

	/**
	 * Takes any collection of objects and returns a map of Key<K> -> List<E> where the Key is the value returned
	 * from the implementation of MapConvertable<K, E>.
	 *
	 * @param entities
	 * @param entityKeyValueConvertable
	 * @param <K>                       Object that represents the Key and must correctly implement the equals() and hashcode()
	 * @param <E>                       Object that is some element/entity within the collection
	 * @return
	 */
	public static <E, K, V> Map<K, Set<V>> convertToMapOfSets(final Collection<E> entities,
	                                                          final EntityKeyValueConvertable<E, K, V> entityKeyValueConvertable) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, Set<V>> map = new HashMap<>();
		for (E entity : entities) {
			K key = entityKeyValueConvertable.getKey(entity);
			addToMapOfKeyToSet(map, key, entityKeyValueConvertable.getValue(entity));
		}

		return map;
	}

	public static <E, K, V> Map<K, V> convertToMap(final Collection<E> entities,
	                                               final EntityKeyValueConvertable<E, K, V> entityKeyValueConvertable) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, V> map = new HashMap<>();
		for (E entity : entities) {
			K key = entityKeyValueConvertable.getKey(entity);
			V value = entityKeyValueConvertable.getValue(entity);

			map.put(key, value);
		}

		return map;
	}

	public static <K, V> void addToMapOfKeyToList(Map<K, List<V>> map, K key, V value) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<V>());
		}

		map.get(key).add(value);
	}

	public static <K, V> void addToMapOfKeyToSet(Map<K, Set<V>> map, K key, V value) {
		if (!map.containsKey(key)) {
			map.put(key, new HashSet<V>());
		}

		map.get(key).add(value);
	}

	public static <K, V> void addAllToMapOfKeyToSet(Map<K, Set<V>> map, K key, Collection<V> value) {
		if (!map.containsKey(key)) {
			map.put(key, new HashSet<V>());
		}

		map.get(key).addAll(value);
	}

	public static <K, V> Map<V, List<K>> invertMapOfList(Map<K, List<V>> map) {
		Map<V, List<K>> invertedMap = new HashMap<>();

		for (Map.Entry<K, List<V>> entry : map.entrySet()) {
			for (V value : entry.getValue()) {
				addToMapOfKeyToList(invertedMap, value, entry.getKey());
			}
		}

		return invertedMap;
	}

    public static <K, V> Map<V, Set<K>> invertMapOfSet(Map<K, Set<V>> map) {
        Map<V, Set<K>> invertedMap = new HashMap<>();

        for (Map.Entry<K, Set<V>> entry : map.entrySet()) {
            for (V value : entry.getValue()) {
                addToMapOfKeyToSet(invertedMap, value, entry.getKey());
            }
        }

        return invertedMap;
    }

	public static <K, V> Map<V, K> invertMap(Map<K, V> map) {
		Map<V, K> invertedMap = new HashMap<>();

		for (Map.Entry<K, V> entry : map.entrySet()) {
			invertedMap.put(entry.getValue(), entry.getKey());
		}

		return invertedMap;
	}

	public static <K, V> Set<V> extractAndFlattenValuesFromMap(final Map<K, ? extends Collection<V>> map) {
		Set<V> values = new HashSet<>();

		for (Map.Entry<K, ? extends Collection<V>> entry : map.entrySet()) {
			values.addAll(entry.getValue());
		}

		return values;
	}

	public static <V> Set<V> flattenCollectionOfCollection(Collection<? extends Collection<V>> collectionOfCollection) {
		Set<V> values = new HashSet<>();

		for (Collection<V> set : collectionOfCollection) {
			values.addAll(set);
		}

		return values;
	}

	public static <E> List<E> unmodifiableList(List<E> elements) {
		if (CollectionUtils.isEmpty(elements)) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableList(elements);
	}

	public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
		if (MapUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(map);
	}
}
