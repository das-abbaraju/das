package com.picsauditing.dao;

import java.io.Serializable;

/**
 * @author eugenp
 */
public interface IDAO< T extends Serializable > extends ICommonOperations< T >{
	
	T findOne( final Long id );

	// save/create/persist
	
	T save( final T entity );
	
	void update( final T entity );
	
}
