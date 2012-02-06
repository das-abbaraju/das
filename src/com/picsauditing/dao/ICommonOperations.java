package com.picsauditing.dao;

import java.io.Serializable;
import java.util.List;

/**
 * @author eugenp
 */
public interface ICommonOperations< T extends Serializable >{
	
	// get

	List< T > findAll();
	
	// save/create/persist
	
	// update
	
	// delete
	
	void delete( final Long entityId );
	
}
