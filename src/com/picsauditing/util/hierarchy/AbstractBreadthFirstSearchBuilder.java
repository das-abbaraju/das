package com.picsauditing.util.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.picsauditing.jpa.entities.BaseTable;

public abstract class AbstractBreadthFirstSearchBuilder<T extends BaseTable> {

	public final Set<Integer> retrieveAllEntityIdsInHierarchy(T entity) {
		List<Integer> entitiesIds = findAllParentEntityIds(entity.getId());
		if (CollectionUtils.isEmpty(entitiesIds)) {
			return Collections.emptySet();
		}
		
		Set<Integer> results = new HashSet<Integer>();
		Queue<Integer> queue = new LinkedList<Integer>();
		
		queue.addAll(entitiesIds);
		while (!queue.isEmpty()) {
			int id = queue.poll();
			
			if (results.contains(id)) {
				continue;
			}
			
			results.add(id);
			entitiesIds = findAllParentEntityIds(id);
			
			if (CollectionUtils.isNotEmpty(entitiesIds)) {
				queue.addAll(entitiesIds);
			}
		}
		
		return results;
	}
	
	protected abstract List<Integer> findAllParentEntityIds(int id);
	
	protected abstract List<Integer> getIdsForAllParentEntities(List<Integer> entities);

}