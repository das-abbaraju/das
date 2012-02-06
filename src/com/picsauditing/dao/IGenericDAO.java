package com.picsauditing.dao;

import java.io.Serializable;

import org.springframework.stereotype.Repository;

public interface IGenericDAO< T extends Serializable > extends IDAO< T >{
	
	void setClazz( final Class< T > clazzToSet );
	
}
