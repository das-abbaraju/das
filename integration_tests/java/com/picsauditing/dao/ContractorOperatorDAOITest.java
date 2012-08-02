package com.picsauditing.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.search.Database;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"ContractorOperatorDAOITest-context.xml"})
public class ContractorOperatorDAOITest {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired private ContractorOperatorDAO contractorOperatorDAO;
	
	@Mock private Database databaseForTesting;
	
	@Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		Whitebox.setInternalState(I18nCache.class, "databaseForTesting", databaseForTesting);
	}

	@Test
	public void testFindForOperators() {
		@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
		Set<Integer> operatorIds = new HashSet() {{
			add(16); add(1068); add(1251); 
		}};
		Set<ContractorOperator> results = contractorOperatorDAO.findForOperators(357, operatorIds);
	}
}
