package com.picsauditing.employeeguard.models.factories;

import com.picsauditing.employeeguard.entities.Project;
import com.picsauditing.employeeguard.models.*;
import com.picsauditing.employeeguard.services.calculator.SkillStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ProjectStatusModelFactoryTest extends ProjectModelFactoryTest {

	ProjectStatusModelFactory projectStatusModelFactory;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		projectStatusModelFactory = new ProjectStatusModelFactory();
	}


}
