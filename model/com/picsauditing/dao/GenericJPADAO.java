package com.picsauditing.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Persistence;
import javax.persistence.Query;


public class GenericJPADAO<T, ID extends Serializable> implements GenericDAO<T, ID> {
	
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
	
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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
			for(String str : params.keySet())
				q.setParameter(str, params.get(str));
			
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
			for(String str : params.keySet())
				q.setParameter(str, params.get(str));
			
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
			for(String str : params.keySet())
				q.setParameter(str, params.get(str));
			
		return q.getResultList();	
	}	
			
}
