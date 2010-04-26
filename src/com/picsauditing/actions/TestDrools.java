package com.picsauditing.actions;

import org.drools.runtime.StatelessKnowledgeSession;

import com.picsauditing.PICS.DroolsSessionFactory;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

import edu.emory.mathcs.backport.java.util.Arrays;

@SuppressWarnings("serial")
public class TestDrools extends PicsActionSupport {

	protected DroolsSessionFactory droolsSessionFactory = null;
	protected ContractorAccountDAO conDao = null;
	
	
	public TestDrools( ContractorAccountDAO conDao, DroolsSessionFactory droolsSessionFactory ) {
		this.droolsSessionFactory = droolsSessionFactory;
		this.conDao = conDao;
	}
	

	@Override
	public String execute() {

		StatelessKnowledgeSession statelessSession = droolsSessionFactory.getStatelessSession();
		
		ContractorAccount con = conDao.find(3);
		ContractorAccount con2 = conDao.find(14);
		
		statelessSession.execute( Arrays.asList(new Object[]{ con, con2 } ) );

		//never do this.  this is just in place to auto reload the rules each request 
		//until we make the sessionfactory support hot swapping.  FOR TESTING ONLY.
		droolsSessionFactory.reset();
		
		return SUCCESS;
	}

}
