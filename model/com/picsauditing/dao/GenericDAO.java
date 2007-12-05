package com.picsauditing.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface GenericDAO<T, ID extends Serializable> {

	T findById(ID id, boolean lock);
	List<T> findAll();
	List<T> findByExample(T exampleInstance,String... excludeProperty);
	List<T> executeNamedQuery(String query, Map<String, Object> params);
	List<T> executeNativeQuery(String query, Map<String, Object> params);
	List<T> executeQuery(String query, Map<String, Object> params);
	T makePersistent(T entity) throws Exception;
	void makeTransient(T entity);
	void flush() throws Exception;
	void clear();
	void close();
	public int getMax();
	public void setMax(int i);
	public int getFirst();
	public void setFirst(int i);
	public void merge(T entity);
	public int getMaxResults();
		
	
}
