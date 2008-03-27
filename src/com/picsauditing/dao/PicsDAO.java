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
	
	/*
		private Class<T> persistentClass;
	private EntityManager entityManager;
	private String persistenceUnit;
	private int max = -1;
	private int first = 0;
	private EntityManagerFactory emf;
	
	
	@SuppressWarnings("unchecked")
	public GenericJPADAO() {
		this.persistentClass = (Class<T>)( (ParameterizedType) getClass().getGenericSuperclass() ).getActualTypeArguments()[0];
	}
	
	protected EntityManager getEntityManager(){
		if(persistenceUnit == null || persistenceUnit.equals(""))
			return null;
		
		if (entityManager == null){
			try{
				if(emf == null)
					emf = Persistence.createEntityManagerFactory(persistenceUnit);
				
				entityManager = emf.createEntityManager();
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		return entityManager;
	}
	
			
	public String getPersistenceUnit() {
			return persistenceUnit;
	}

	public void setPersistenceUnit(String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}
	
	public void clear() {
		getEntityManager().clear();
	}

	@SuppressWarnings("unchecked")
	public List findAll() {
		Query ejbQuery = getEntityManager().createQuery("select t from " + getPersistentClass().getSimpleName() + " t" );
		if(max > 0){
			ejbQuery.setFirstResult(first);
			ejbQuery.setMaxResults(max);
		}
		return ejbQuery.getResultList();		
	}

	@SuppressWarnings("unchecked")
	public List findByExample(T exampleInstance, String... excludeProperty) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	public T findById(Serializable id, boolean lock) {
		T entity;
		
		entity =  getEntityManager().find(getPersistentClass(), id);
		
		if (lock)
			getEntityManager().lock(entity, LockModeType.READ);
		
				
		return entity;
	}

	public void flush() throws Exception {
		EntityTransaction tx = getEntityManager().getTransaction();
		try{			
			tx.begin();	
			getEntityManager().flush();
			tx.commit();
		} catch (RuntimeException ex) {
			try {
				tx.rollback();
			} catch (RuntimeException rbEx) {
			  throw new Exception("Couldn't roll back transaction");
			}
			throw ex;
			} finally {
				//getEntityManager().close();
				//entityManager = null;
			}
	}

	@SuppressWarnings("unchecked")
	public T makePersistent(T entity) throws Exception {		
		T mergedEntity;
		EntityTransaction tx = getEntityManager().getTransaction();
		try{			
			tx.begin();
			mergedEntity = getEntityManager().merge(entity);
			tx.commit();
		} catch (RuntimeException ex) {
			try {
			tx.rollback();
			} catch (RuntimeException rbEx) {
			  throw new Exception("Couldn't roll back transaction");
			}
			throw ex;
			} finally {
				//getEntityManager().close();
				//entityManager = null;
			}
		
		return mergedEntity;
	}
	
	public void makePersistent(List<T> list)throws Exception{
		EntityTransaction tx = getEntityManager().getTransaction();
		try{			
			tx.begin();
			for(T t : list)
				getEntityManager().merge(t);
			tx.commit();
		} catch (RuntimeException ex) {
			try {
			tx.rollback();
			} catch (RuntimeException rbEx) {
			  throw new Exception("Couldn't roll back transaction");
			}
			throw ex;
			} finally {
				//getEntityManager().close();
				//entityManager = null;
			}
	}

	public void makeTransient(T entity) {
		getEntityManager().remove(entity);
	}

	public void close() {
		getEntityManager().close();
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}	
	
	public void merge(T entity){
		getEntityManager().merge(entity);
	}
	
	public int getMaxResults(){
		Query ejbQuery = getEntityManager().createQuery("select Count(t) from " + getPersistentClass().getName() + " t" );
		String str = ejbQuery.getSingleResult().toString();
		return Integer.parseInt(str);
		
	}

	@Override
	public List executeNamedQuery(String query, Map<String, Object> params) {
		Query q = getEntityManager().createNamedQuery(query);
		if(max > 0 ) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
		
		if(params != null)
			setQueryParams(q, params);
			
		return q.getResultList();	
	}
	
	@Override
	public List executeNativeQuery(String query, Map<String, Object> params) {
		Query q = getEntityManager().createNativeQuery(query, getPersistentClass());
		if(max > 0 ) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
		
		if(params != null)
			setQueryParams(q, params);
			
		return q.getResultList();	
	}
	
	@Override
	public List executeNativeQuery(String queryName) {
		Query q = getEntityManager().createNamedQuery(queryName);
		if(max > 0 ) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
				
		return q.getResultList();	
	}
	
	@Override
	public List executeNativeQuery(String query, Map<String, Object> params, String mappingName) {
		Query q = getEntityManager().createNativeQuery(query, mappingName);
		if(max > 0 ) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
		
		if(params != null)
			setQueryParams(q, params);
			
		return q.getResultList();	
	}
	
	@Override
	public List executeQuery(String query, Map<String, Object> params) {
		Query q = getEntityManager().createQuery(query);
		
		if(max > 0 ) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
		
		if(params != null)
			setQueryParams(q, params);
			
		return q.getResultList();	
	}
	
	
	@Override
	public Object executeScalarQuery(String query, Map<String, Object> params){
		Query q = getEntityManager().createQuery(query);
		if(max > 0 ) {
			q.setFirstResult(first);
			q.setMaxResults(max);
		}
		
		if(params != null)
			setQueryParams(q, params);
			
		return q.getSingleResult();
	}
	
	private void setQueryParams(Query q, Map<String, Object> params ){		
		for(String str : params.keySet()){
			Object param = params.get(str);
			if(param instanceof Date)
				q.setParameter(str, (Date)params.get(str), TemporalType.DATE);
			else if(param instanceof Calendar)
				q.setParameter(str, (Calendar)params.get(str), TemporalType.DATE);
			else
				q.setParameter(str, params.get(str));
		}
	}

	 */
}
