package com.picsauditing.securitysession.util.hierarchy;

import java.util.Set;

public interface HierarchyBuilder {

	Set<Integer> retrieveAllEntityIdsInHierarchy(int entityId);
	
}
