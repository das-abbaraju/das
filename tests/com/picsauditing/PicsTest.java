package com.picsauditing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.EntityManager;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.PicsDAO;

public abstract class PicsTest {
	@Mock protected EntityManager em;
	
	protected void autowireEMInjectedDAOs(Object objectToAutowire) throws InstantiationException, IllegalAccessException {
		Class<?> classOfObject = objectToAutowire.getClass();
		while(null != classOfObject) {
			autowireEMInjectedDAOs(objectToAutowire, classOfObject);
			classOfObject = classOfObject.getSuperclass();
		}
	}		
	
	protected void autowireEMInjectedDAOs(Object objectToAutowire, Class<?> classOfObject) throws InstantiationException, IllegalAccessException {		
		Field[] fields = classOfObject.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			wireDAOAnnotations(objectToAutowire, field);
		}
	}

	protected void wireDAOAnnotations(Object objectToAutowire, Field field) throws InstantiationException, IllegalAccessException {
		Annotation[] annotations = field.getAnnotations();
		for (int j = 0; j < annotations.length; j++) {
			Annotation annotation = annotations[j];
			wireIfDAOAnnotation(objectToAutowire, field, annotation);
		}
	}

	protected void wireIfDAOAnnotation(Object objectToAutowire, Field field, Annotation annotation)
			throws InstantiationException, IllegalAccessException {
		if (annotation instanceof Autowired) {
			Class<?> classOfField = field.getType();
			if (PicsDAO.class.isAssignableFrom(classOfField)) {
				PicsDAO dao = (PicsDAO)classOfField.newInstance();
				dao.setEntityManager(em);
				forceSetPrivateField(objectToAutowire, field.getName(), dao);
			}
		}
	}
	
	protected void forceSetPrivateField(Object objectToForceSet, String fieldname, Object fieldValue) {
		Class<?> classOfObject = objectToForceSet.getClass();
		forceSetPrivateField(classOfObject, objectToForceSet, fieldname, fieldValue);
	}
	
	protected void forceSetPrivateField(Class<?> classOfObject, Object objectToForceSet, String fieldname, Object fieldValue) {
	    try {
	        Field field = classOfObject.getDeclaredField(fieldname);
	        field.setAccessible(true);
	        field.set(objectToForceSet, fieldValue);
	      } catch (NoSuchFieldException x) {
	    	  Class<?> superClass = classOfObject.getSuperclass();
	    	  if (null == superClass) {
	    		  x.printStackTrace();
	    	  } else {
	    		  forceSetPrivateField(superClass, objectToForceSet, fieldname, fieldValue);	    		
	    	  }
	      } catch (IllegalArgumentException x) {
	    	  x.printStackTrace();
	      } catch (IllegalAccessException x) {
	    	  x.printStackTrace();
	      }		
	}
}
