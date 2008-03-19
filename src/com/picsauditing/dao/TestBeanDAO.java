package com.picsauditing.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.actions.TestBean;

@Transactional
public class TestBeanDAO {

	EntityManager em = null;
	
	@PersistenceContext
	public void setEntityManager( EntityManager em )
	{
		this.em = em;
	}
	
	public TestBean save( TestBean o )
	{
		if( o.getId() == 0 )
		{
			em.persist(o);
		}
		else
		{
			o = em.merge(o);
		}
		return o;
	}
	
}
