package com.picsauditing.actions;

import org.drools.runtime.StatelessKnowledgeSession;

import com.picsauditing.PICS.DroolsSessionFactory;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TestDrools extends PicsActionSupport {

	protected DroolsSessionFactory droolsSessionFactory = null;
	protected ContractorAccountDAO conDao = null;
	
	protected boolean reset = false;
	
	
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

		if( isReset() )
			droolsSessionFactory.reset();
		
		return SUCCESS;
	}


	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}
	
}
