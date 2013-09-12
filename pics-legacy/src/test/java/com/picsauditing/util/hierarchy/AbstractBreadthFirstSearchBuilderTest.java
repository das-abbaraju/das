package com.picsauditing.util.hierarchy;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.Utilities;


public class AbstractBreadthFirstSearchBuilderTest {
	
	@Mock
	AbstractBreadthFirstSearchBuilder abtractBuilder;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testBreadthFirstSearchBuilder_CircularDependencies() {
		BreadthFirstSearchBuilder bfs = new BreadthFirstSearchBuilder();
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(2, 3, 4, 5, 6), bfs.retrieveAllEntityIdsInHierarchy(1)));
	}
	
	@Test
	public void testBreadthFirstSearchBuilder_NoDependencies() {
		when(abtractBuilder.findAllParentEntityIds(anyInt())).thenReturn(null);
		when(abtractBuilder.getIdsForAllParentEntities(anyListOf(Integer.class))).thenReturn(null);
		
		Set<Integer> results = abtractBuilder.retrieveAllEntityIdsInHierarchy(1);
		
		assertNotNull(results);
		assertTrue(results.isEmpty());
	}
	
	@Test
	public void testBreadthFirstSearchBuilder_OneLevelOfDependencies() {
		when(abtractBuilder.findAllParentEntityIds(anyInt())).thenReturn(Arrays.asList(1, 2, 3));
		when(abtractBuilder.getIdsForAllParentEntities(anyListOf(Integer.class))).thenReturn(null);
		
		Set<Integer> results = abtractBuilder.retrieveAllEntityIdsInHierarchy(10);
		
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(3, 2, 1), results));
	}
	
	/**
	 * "Fake" implementation to validate the implementation of the
	 * AbstractBreadthFirstSearchBuilder.
	 * 
	 * Parents of 1 = 2, 3, 4 
	 * Parents of 2 = 3, 4 
	 * Parents of 3 = 2, 4 
	 * Parents of 4 = 2, 3, 5, 6 
	 * Parents of 5 = None 
	 * Parents of 6 = None
	 */
	public static class BreadthFirstSearchBuilder extends AbstractBreadthFirstSearchBuilder {

		@Override
		protected List<Integer> findAllParentEntityIds(int id) {
			return (getParentIds(id));
		}

		@Override
		protected List<Integer> getIdsForAllParentEntities(List<Integer> entities) {
			if (CollectionUtils.isEmpty(entities)) {
				return Collections.emptyList();
			}

			List<Integer> ids = new ArrayList<Integer>();
			for (Integer id : entities) {
				ids.addAll(getParentIds(id));
			}

			return ids;
		}

		private List<Integer> getParentIds(int id) {
			switch (id) {

			case 1:
				return Arrays.asList(2, 3, 4);

			case 2:
				return Arrays.asList(3, 4);

			case 3:
				return Arrays.asList(2, 4);

			case 4:
				return Arrays.asList(2, 3, 5, 6);

			}

			return Collections.emptyList();
		}

	}

}
