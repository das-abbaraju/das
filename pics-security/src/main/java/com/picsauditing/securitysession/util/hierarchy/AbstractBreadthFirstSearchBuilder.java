package com.picsauditing.securitysession.util.hierarchy;

import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public abstract class AbstractBreadthFirstSearchBuilder implements HierarchyBuilder {

	public final Set<Integer> retrieveAllEntityIdsInHierarchy(int entityId) {
		List<Integer> entitiesIds = findAllParentEntityIds(entityId);
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