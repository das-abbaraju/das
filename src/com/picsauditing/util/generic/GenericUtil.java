package com.picsauditing.util.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(GenericUtil.class);
	
	/**
	 * Provided to create an instance of a generic type.  This should be used when
	 * you are in a Generic method that requires a new instance of a type to be
	 * created during runtime, but the 
	 * 
	 * @param type
	 * @return
	 */
	public static <T> T newInstance(Class<T> type) {
		if (type == null) {
			return null;
		}
		
		try {
			return type.newInstance();
		} catch (Exception e) {
			logger.warn("Error while creating a new instance of class type {}", type.getName(), e);
		}
		
		return null;
	}

}
