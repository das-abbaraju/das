package com.picsauditing;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.TestCase;

/**
 * Abstract Class useful for testing classes/methods that
 * make use of the DataBase Class (and therefore DBBean)
 * as those attempt to grab a dataSource from the env settings
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/tests.xml")
public abstract class PicsDBTest extends TestCase {
	
	private static ClassPathXmlApplicationContext context;
	
	@BeforeClass
	public static void testJndi( ) throws Exception {

		context = new ClassPathXmlApplicationContext("tests.xml");

		SimpleNamingContextBuilder builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();

		DataSource ds = (DataSource) context.getBean("dataSource");

		builder.bind("java:comp/env/jdbc/pics", ds);

	}

}
