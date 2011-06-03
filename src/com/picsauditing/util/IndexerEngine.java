package com.picsauditing.util;

import java.util.List;
import java.util.Set;

import com.picsauditing.jpa.entities.Indexable;

public interface IndexerEngine {

	/**
	 * Inserts a single record into the index
	 * 
	 * @param clazz
	 *            Class of record to index
	 * @param id
	 *            id of record to index
	 */
	public void runSingle(Indexable toIndex);

	/**
	 * Inserts all records into the index
	 * 
	 * @param clazz
	 *            Class of record to index
	 */
	public void run(Class<? extends Indexable> clazz);

	/**
	 * Inserts all records for each class in <code>toIndex</code> into the
	 * index.
	 * 
	 * @param toIndex
	 *            List of classes to index
	 */
	public void runAll(Set<Class<? extends Indexable>> toIndex);

	/**
	 * Deletes all records in list from the index
	 * 
	 * @param listToDelete
	 *            List of records to delete
	 */
	public void delete(List<Indexable> listToDelete);

	/**
	 * Deletes one record from the index
	 * 
	 * @param toDelete
	 *            Record to delete
	 */
	public void deleteSingle(Indexable toDelete);

	/**
	 * Updates the <code>needsIndexing</code> field on the entities of class
	 * <code>clazz</code>
	 * 
	 * @param id
	 *            id of entity to update
	 * @param clazz
	 *            class of entity
	 */
	public void updateIndex(int id, Class<? extends Indexable> clazz);

	/**
	 * Updates the <code>needsIndexing</code> field on the entities of class
	 * <code>clazz</code>
	 * 
	 * @param saved
	 *            set of ids to save
	 * @param clazz
	 *            class of record
	 */
	public void updateIndex(Set<Integer> saved, Class<? extends Indexable> clazz);

	public Set<Class<? extends Indexable>> getEntries();

}
