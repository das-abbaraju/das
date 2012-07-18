package com.picsauditing.jpa.entities;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.dao.FlagCriteriaDAO;

/*
 * The point of these tests is to test a hibernate upgrade from 3.3.0.ga to 3.6.10.Final in which TypeFactory.basic was removed, thereby 
 * breaking our EnumMapperWithEmptyStrings.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"FlagCriteriaITest-context.xml"})
public class FlagCriteriaITest {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired private FlagCriteriaDAO flagCriteriaDAO;
	
	@Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Before
	public void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testEnumMapping_NotNullOshaType() throws Exception {
		List<FlagCriteria> allTestFlagCriteria = flagCriteriaDAO.findWhere(" oshaType is not null and oshaType <> ''");
		for (FlagCriteria flagCriteria : allTestFlagCriteria) {
			assertThat(flagCriteria.getOshaType(), is(notNullValue()));
		}
		assertTrue(allTestFlagCriteria.size() > 0);
	}
	
	@Test
	public void testEnumMapping_NullOshaType() throws Exception {
		List<FlagCriteria> allTestFlagCriteria = flagCriteriaDAO.findWhere(" oshaType is null");
		for (FlagCriteria flagCriteria : allTestFlagCriteria) {
			assertThat(flagCriteria.getOshaType(), is(nullValue()));
		}
		assertTrue(allTestFlagCriteria.size() > 0);
	}
	
	@Test
	public void testEnumMapping_EmptyStringOshaType() throws Exception {
		List<FlagCriteria> allTestFlagCriteria = flagCriteriaDAO.findWhere(" oshaType = ''");
		for (FlagCriteria flagCriteria : allTestFlagCriteria) {
			assertThat(flagCriteria.getOshaType(), is(nullValue()));
		}
		assertTrue(allTestFlagCriteria.size() > 0);
	}
	
	@Test
	public void testEnumMapping_UnknownStringOshaType() throws Exception {
		jdbcTemplate.update("update flag_criteria set oshaType = 'BAD' where oshaType = ''");
		
		List<FlagCriteria> allTestFlagCriteria = flagCriteriaDAO.findWhere(" oshaType = 'BAD'");
		for (FlagCriteria flagCriteria : allTestFlagCriteria) {
			assertThat(flagCriteria.getOshaType(), is(nullValue()));
		}
		assertTrue(allTestFlagCriteria.size() > 0);
		
		jdbcTemplate.update("update flag_criteria set oshaType = '' where oshaType = 'BAD'");
	}
	
	@Test
	public void testFindAllReturnsAll() throws Exception {
		List<FlagCriteria> allTestFlagCriteria = flagCriteriaDAO.findAll();
		int rowCount = jdbcTemplate.queryForInt("select count(*) from flag_criteria");
		assertEquals(rowCount, allTestFlagCriteria.size());
	}
}
