package com.picsauditing.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

@Transactional
abstract public class PicsDAO {
	protected EntityManager em;
	protected QueryMetaData queryMetaData = null;
	
	
	@PersistenceContext
	public void setEntityManager( EntityManager em )
	{
		this.em = em;
	}


	public QueryMetaData getQueryMetaData() {
		return queryMetaData;
	}

	public void setQueryMetaData(QueryMetaData queryMetaData) {
		this.queryMetaData = queryMetaData;
	}
	
	
	protected void applyQueryMetaData( Query query )
	{
		QueryMetaData qmd = getQueryMetaData();
		if( qmd != null )
		{
			if( qmd.getMaxRows() != -1 )
			{
				query.setMaxResults(qmd.getMaxRows());
			}
			
			if( qmd.getStartRow() != -1  )
			{
				query.setFirstResult( qmd.getStartRow() );
			}
		}
	}
	
	protected static void setOptionalParameter(Query query, String name, Object value) {
		try {
			query.setParameter(name, value);
		} catch (IllegalArgumentException e) {}
	}
}
