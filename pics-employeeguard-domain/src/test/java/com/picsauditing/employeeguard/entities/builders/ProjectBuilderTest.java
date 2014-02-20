package com.picsauditing.employeeguard.entities.builders;

import com.picsauditing.employeeguard.entities.Project;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProjectBuilderTest {

    private static final String PROJECT_NAME = "Name of Project";
    private static final String PROJECT_LOCATION = "Location of Project";

    @Test
    public void testBuild() {
        Project project = new ProjectBuilder().createdBy(4).name(PROJECT_NAME).location(PROJECT_LOCATION).build();

        verifyProject(project);
    }

    private void verifyProject(Project project) {
        assertEquals(PROJECT_LOCATION, project.getLocation());
        assertEquals(PROJECT_NAME, project.getName());
		assertEquals(4, project.getCreatedBy());
    }
}
