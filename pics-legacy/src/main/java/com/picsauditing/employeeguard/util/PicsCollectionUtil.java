package com.picsauditing.employeeguard.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class PicsCollectionUtil {

	public interface MapConvertable<K, E> {

		K getKey(E entity);

	}

	public interface Identitifable<E, ID> {

		ID getId(E element);

	}

	public interface EntityKeyValueConvertable<E, K, V> {

		K getKey(E entity);

		V getValue(E entity);
	}

	public interface PropertyExtractor<ENTITY, PROPERTY> {
		PROPERTY getProperty(ENTITY entity);
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
		if (org.springframework.util.CollectionUtils.isEmpty(entities)) {
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
		if (org.springframework.util.CollectionUtils.isEmpty(entities)) {
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
		if (org.springframework.util.CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, Set<E>> map = new HashMap<>();
		for (E entity : entities) {
			K key = mapConverter.getKey(entity);
			addToMapOfKeyToSet(map, key, entity);
		}

		return map;
	}

	public static <E, ID> Set<ID> getIdsFromCollection(Collection<E> elements, Identitifable<E, ID> identitifable) {
		if (org.springframework.util.CollectionUtils.isEmpty(elements)) {
			return Collections.emptySet();
		}

		Set<ID> ids = new HashSet<>();
		for (E element : elements) {
			ids.add(identitifable.getId(element));
		}

		return ids;
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
		if (org.springframework.util.CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, Set<V>> map = new HashMap<>();
		for (E entity : entities) {
			K key = entityKeyValueConvertable.getKey(entity);
			addToMapOfKeyToSet(map, key, entityKeyValueConvertable.getValue(entity));
		}

		return map;
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
	public static <E, K, V> Map<K, List<V>> convertToMapOfLists(final Collection<E> entities,
																final EntityKeyValueConvertable<E, K, V> entityKeyValueConvertable) {
		if (org.springframework.util.CollectionUtils.isEmpty(entities)) {
			return Collections.emptyMap();
		}

		Map<K, List<V>> map = new HashMap<>();
		for (E entity : entities) {
			K key = entityKeyValueConvertable.getKey(entity);
			addToMapOfKeyToList(map, key, entityKeyValueConvertable.getValue(entity));
		}

		return map;
	}

	public static <E, K, V> Map<K, V> convertToMap(final Collection<E> entities,
												   final EntityKeyValueConvertable<E, K, V> entityKeyValueConvertable) {
		if (org.springframework.util.CollectionUtils.isEmpty(entities)) {
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

	public static <K, V> void addAllToMapOfKeyToList(Map<K, List<V>> map, K key, Collection<V> value) {
		if (!map.containsKey(key)) {
			map.put(key, new ArrayList<V>());
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
		if (org.springframework.util.CollectionUtils.isEmpty(elements)) {
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

	public static <V> Collection<V> unmodifiableCollection(Collection<V> collection) {
		if (org.springframework.util.CollectionUtils.isEmpty(collection)) {
			return Collections.emptySet();
		}

		return Collections.unmodifiableCollection(collection);
	}

	public static <V> Set<V> mergeCollections(Collection<V>... collections) {
		Set<V> uniqueCollection = new HashSet<>();

		for (Collection collection : collections) {
			uniqueCollection.addAll(collection);
		}

		return uniqueCollection;
	}

	public static <V> Set<V> mergeCollectionOfCollections(Collection<? extends Collection<V>>... collections) {
		Set<V> uniqueCollection = new HashSet<>();

		for (Collection collection : collections) {
			// Collection of collections
			Iterator<Collection<V>> collectionIterator = collection.iterator();
			while (collectionIterator.hasNext()) {
				for (V value : collectionIterator.next()) {
					uniqueCollection.add(value);
				}
			}
		}

		return uniqueCollection;
	}

	public static <K, V> Map<K, List<V>> mergeMapOfLists(final Map<K, List<V>>... maps) {
		if (ArrayUtils.isEmpty(maps)) {
			return Collections.emptyMap();
		}

		Map<K, List<V>> mergedValues = new HashMap<>();
		for (Map<K, List<V>> map : maps) {
			if (MapUtils.isEmpty(map)) {
				continue;
			}

			for (K key : map.keySet()) {
				if (!mergedValues.containsKey(key)) {
					mergedValues.put(key, new ArrayList<V>());
				}

				if (CollectionUtils.isNotEmpty(map.get(key))) {
					mergedValues.get(key).addAll(map.get(key));
				}
			}
		}

		return mergedValues;
	}

	public static <K, V> Map<K, Set<V>> mergeMapOfSets(final Map<K, Set<V>> map1, final Map<K, Set<V>> map2) {
		Map<K, Set<V>> mergedValues = copyMapOfSets(map1);

		for (Map.Entry<K, Set<V>> entrySet : map2.entrySet()) {
			K key = entrySet.getKey();

			if (mergedValues.containsKey(key)) {
				mergedValues.get(key).addAll(map2.get(key));
			} else {
				mergedValues.put(key, map2.get(key));
			}
		}

		return mergedValues;
	}

	public static <K, V> Map<K, Set<V>> copyMapOfSets(final Map<K, Set<V>> map) {
		if (MapUtils.isEmpty(map)) {
			return new HashMap<>();
		}

		Map<K, Set<V>> copy = new HashMap<>();
		for (K key : map.keySet()) {
			copy.put(key, new HashSet<>(map.get(key)));
		}

		return copy;
	}

	public static <E, V> Map<E, V> mergeMaps(final Map<E, V> map1, final Map<E, V> map2) {
		if (MapUtils.isEmpty(map1) && MapUtils.isEmpty(map2)) {
			return Collections.emptyMap();
		}

		if (MapUtils.isEmpty(map1)) {
			return map2;
		}

		if (MapUtils.isEmpty(map2)) {
			return map1;
		}

		Map<E, V> mergedValues = new HashMap<>(map1);

		mergedValues.putAll(map2);

		return mergedValues;
	}

	public static <ENTITY, PROPERTY> Set<PROPERTY> extractPropertyToSet(final Collection<ENTITY> entities,
																		final PropertyExtractor<ENTITY, PROPERTY> propertyExtractor) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptySet();
		}

		Set<PROPERTY> properties = new HashSet<>();

		for (ENTITY entity : entities) {
			properties.add(propertyExtractor.getProperty(entity));
		}

		return properties;
	}

	public static <ENTITY, PROPERTY> List<PROPERTY> extractPropertyToList(final Collection<ENTITY> entities,
																		  final PropertyExtractor<ENTITY, PROPERTY> propertyExtractor) {
		List<PROPERTY> properties = new ArrayList<>();

		for (ENTITY entity : entities) {
			properties.add(propertyExtractor.getProperty(entity));
		}

		return properties;
	}

	public interface CollectionToMapConverter<R, C, V> {

		R getRow(V value);

		C getColumn(V value);

	}

	public static <R, C, V> Map<R, Map<C, V>> convertToMapOfMaps(final Collection<V> values,
																 final CollectionToMapConverter<R, C, V> converter) {
		if (CollectionUtils.isEmpty(values) || converter == null) {
			return Collections.emptyMap();
		}

		Map<R, Map<C, V>> result = new HashMap<>();
		for (V value : values) {
			R row = converter.getRow(value);
			C column = converter.getColumn(value);

			if (!result.containsKey(row)) {
				result.put(row, new HashMap<C, V>());
			}

			result.get(row).put(column, value);
		}

		return result;
	}

	public static <E, K, V> Map<K, Set<V>> reduceMapOfCollections(final Map<K, ? extends Collection<E>> keyEntityMap,
																  final Map<E, ? extends Collection<V>> entityValueMap) {

		if (MapUtils.isEmpty(keyEntityMap) || MapUtils.isEmpty(entityValueMap)) {
			return Collections.emptyMap();
		}

		Map<K, Set<V>> result = new HashMap<>();
		for (K key : keyEntityMap.keySet()) {
			if (!result.containsKey(key)) {
				result.put(key, new HashSet<V>());
			}

			result.get(key).addAll(getAllEntityValues(keyEntityMap.get(key), entityValueMap));
		}

		return result;
	}

	public static <E, K, V> Map<K, List<V>> reduceMaps(final Map<K, ? extends Collection<E>> keyEntityMap,
													   final Map<E, V> entityValueMap) {
		if (MapUtils.isEmpty(keyEntityMap) || MapUtils.isEmpty(entityValueMap)) {
			return Collections.emptyMap();
		}

		Map<K, List<V>> result = new HashMap<>();
		for (K key : keyEntityMap.keySet()) {
			if (!result.containsKey(key)) {
				result.put(key, new ArrayList<V>());
			}

			result.get(key).addAll(getEntityValues(keyEntityMap.get(key), entityValueMap));
		}

		return result;
	}

	public static <E, K, V> Map<K, Set<V>> reduceMapsForPairKeyMap(final Map<K, E> keyEntityMap,
																   final Map<E, ? extends Collection<V>> entityValueMap) {
		if (MapUtils.isEmpty(keyEntityMap) || MapUtils.isEmpty(entityValueMap)) {
			return Collections.emptyMap();
		}

		Map<K, Set<V>> result = new HashMap<>();
		for (K key : keyEntityMap.keySet()) {
			if (!result.containsKey(key)) {
				result.put(key, new HashSet<V>());
			}

			E entity = keyEntityMap.get(key);
			if (entityValueMap.containsKey(entity)) {
				result.get(key).addAll(entityValueMap.get(entity));
			}
		}

		return result;
	}

	public static <E, K, V> Map<K, List<V>> reduceMap(final Map<K, E> keyEntityMap,
													  final Map<E, V> entityValueMap) {
		if (MapUtils.isEmpty(keyEntityMap) || MapUtils.isEmpty(entityValueMap)) {
			return Collections.emptyMap();
		}

		Map<K, List<V>> result = new HashMap<>();
		for (K key : keyEntityMap.keySet()) {
			if (!result.containsKey(key)) {
				result.put(key, new ArrayList<V>());
			}

			result.get(key).add(entityValueMap.get(keyEntityMap.get(key)));
		}

		return result;
	}

	private static <E, V> Collection<V> getAllEntityValues(final Collection<E> entities,
														   final Map<E, ? extends Collection<V>> entityValueMap) {
		Collection<V> values = new ArrayList<>();
		if (CollectionUtils.isEmpty(entities)) {
			return values;
		}

		for (E entity : entities) {
			if (entityValueMap.containsKey(entity)) {
				values.addAll(entityValueMap.get(entity));
			}
		}

		return values;
	}

	private static <E, V> Collection<V> getEntityValues(final Collection<E> entities,
														final Map<E, V> entityValueMap) {
		Collection<V> values = new ArrayList<>();

		for (E entity : entities) {
			if (entityValueMap.containsKey(entity)) {
				values.add(entityValueMap.get(entity));
			}
		}

		return values;
	}

	public static <K, V> Map<K, Set<V>> addKeys(final Map<K, Set<V>> map, final Collection<K> allKeys) {
		Map<K, Set<V>> completeMap = new HashMap<>();
		if (MapUtils.isNotEmpty(map)) {
			completeMap = copyMapOfSets(map);
		}

		for (K key : allKeys) {
			if (!completeMap.containsKey(key)) {
				completeMap.put(key, new HashSet<V>());
			}
		}

		return completeMap;
	}

	public interface KeyTransformable<K, N> {

		N getNewKey(K key);

	}

	public static <K, V, N> Map<N, Set<V>> transformMap(final Map<K, Set<V>> map,
														final KeyTransformable<K, N> keyTransformable) {
		if (MapUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		Map<N, Set<V>> transformedMap = new HashMap<>();
		for (K key : map.keySet()) {
			transformedMap.put(keyTransformable.getNewKey(key), map.get(key));
		}

		return transformedMap;
	}

	public static <E extends Comparable> Set<E> sortSet(final Set<E> set) {
		if (CollectionUtils.isEmpty(set)) {
			return Collections.emptySet();
		}

		List<E> listToSort = new ArrayList<>(set);
		Collections.sort(listToSort);

		return new LinkedHashSet<>(listToSort);
	}
}
