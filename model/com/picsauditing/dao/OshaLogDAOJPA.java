package com.picsauditing.dao;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.picsauditing.jpa.entities.*;

@SuppressWarnings("unchecked")
public class OshaLogDAOJPA extends GenericJPADAO<OshaLog, Short> implements OshaLogDAO {

	public OshaLog editLog(Map parameters, String oID) throws Exception{
		OshaLog o = (OshaLog)findById(Short.parseShort(oID),false);
		if(o == null)
			return o;
		
	    for(Object obj : parameters.keySet()){
	    	String name = (String)obj;	    	
	    	if(name.equals("action"))
	    		continue; 
	    	BeanUtils.setProperty(o, name, parameters.get(name));	    	
	    }
	    	       
	    return o;
	  }
	
	
}

