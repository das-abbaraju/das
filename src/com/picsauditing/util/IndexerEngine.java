package com.picsauditing.util;

import java.util.List;
import java.util.Set;

import com.picsauditing.jpa.entities.Indexable;

public interface IndexerEngine {

	/**
	 * Inserts a single record into the index
	 */
	public void runSingle(Class<? extends Indexable> clazz, int id);

	/**
	 * Inserts all records for a given type
	 * 
	 */
	public void run(Class<? extends Indexable> clazz);

	/**
	 * Inserts all records for all types
	 */
	public void runAll(Set<Class<? extends Indexable>> toIndex);

	public void delete(List<Indexable> listToDelete);

	public void deleteSingle(Indexable toDelete);

	public void updateIndex(int id, Class<? extends Indexable> clazz);

	/**
	 * Updates the index using the string
	 * 
	 * @param indexString
	 */
	public void updateIndex(Set<Integer> saved, Class<? extends Indexable> clazz);

	public Set<Class<? extends Indexable>> getEntries();

}
