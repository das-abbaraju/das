package com.picsauditing.util.hierarchy;


import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.CollectionUtils;

import com.picsauditing.PICS.Utilities;

public class GroupHierarchyBuilderTest {
	
	@Spy
	GroupHierarchyBuilder bfs;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testBreadthFirstSearchBuilder_CircularDependencies() {
		doAnswer(buildAnswerForFindParentIds()).when(bfs).findAllParentEntityIds(anyInt());
		doAnswer(buildAnswerForFindAllParentIds()).when(bfs).getIdsForAllParentEntities(anyListOf(Integer.class));
	
		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(2, 3, 4, 5, 6), bfs.retrieveAllEntityIdsInHierarchy(1)));
	}
	
	private Answer<List<Integer>> buildAnswerForFindParentIds() {
		return new Answer<List<Integer>>() {

			@Override
			public List<Integer> answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				int id = (Integer) args[0];
				return getParentIds(id);
			}
		};
	}
	
	private Answer<List<Integer>> buildAnswerForFindAllParentIds() {
		return new Answer<List<Integer>>() {

			@Override
			public List<Integer> answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				
				@SuppressWarnings("unchecked")
				List<Integer> ids = (List<Integer>) args[0];
				
				if (CollectionUtils.isEmpty(ids)) {
					return Collections.emptyList();
				}

				List<Integer> parentIds = new ArrayList<Integer>();
				for (Integer id : parentIds) {
					parentIds.addAll(getParentIds(id));
				}

				return parentIds;
			}
			
		};
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
