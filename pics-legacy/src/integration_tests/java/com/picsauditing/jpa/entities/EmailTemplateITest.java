package com.picsauditing.jpa.entities;

import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.dao.EmailTemplateDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"EmailTemplateITest-context.xml"})
public class EmailTemplateITest {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired private EmailTemplateDAO emailTemplateDAO;
	
	@Autowired 
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
	
	@Before
	public void setUp() throws Exception {
		//MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindAllEmailTemplates() throws Exception {
		List<EmailTemplate> allTemplates = emailTemplateDAO.findAll();
	}
}
