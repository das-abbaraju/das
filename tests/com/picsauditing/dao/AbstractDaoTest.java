package com.picsauditing.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
//import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

public abstract class AbstractDaoTest extends TestCase {
	private static final String TEST_PERSISTENCE_UNIT = "punit";
	private EntityManagerFactory emf;
	protected EntityManager em;
	
	public AbstractDaoTest () {
		super();
	}
	
	public void setUp() throws Exception {
		super.setUp();
		emf = Persistence.createEntityManagerFactory(TEST_PERSISTENCE_UNIT);
		em = emf.createEntityManager();
	}
	
	public void tearDown() {
		em.close();
		emf.close();
	}
}
