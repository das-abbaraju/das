package com.picsauditing.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.picsauditing.dao.TestBeanDAO;

public class FirstStrutsAction extends ActionSupport 
{
	protected TestBean testBean = null;
	protected TestBeanDAO dao = null;
	
	public FirstStrutsAction( TestBeanDAO dao )
	{
		this.dao = dao;
	}
	
	public String execute() throws Exception
	{
		
		if( testBean == null )
		{
			testBean = new TestBean();
			
			testBean.setGreeting("this is a field on a pojo.  I dare you to send me to the database.");
		}
		else
		{
			dao.save(testBean);
		}
		
		return SUCCESS;
	}

	public TestBean getTestBean() {
		return testBean;
	}

	public void setTestBean(TestBean testBean) {
		this.testBean = testBean;
	}



}
