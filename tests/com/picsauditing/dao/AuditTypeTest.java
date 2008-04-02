package com.picsauditing.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.picsauditing.jpa.entities.AuditType;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/tests.xml")
public class AuditTypeTest 
{
	@Autowired 
	AuditTypeDAO dao = null;
	
	
	@Test
	public void showDaoUsage()
	{ 
		List<AuditType> auditTypes = dao.findAll();
		
		for( AuditType at : auditTypes )
		{
			System.out.println(at.getAuditName());	
		}
	}
}
