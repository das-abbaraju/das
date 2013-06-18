package com.picsauditing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.dao.BaseTableDAO;
import com.picsauditing.dao.PicsDAO;

public class PicsTestUtil {
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public static <T> void autowireDAOsFromDeclaredMocks(Object objectToAutowire, Object toTakeMockDaosFrom) 
			throws InstantiationException, IllegalAccessException {
		Class<T> classOfToObject = (Class<T>) objectToAutowire.getClass();
		Class<T> classOfFromObject = (Class<T>) toTakeMockDaosFrom.getClass();
		Field[] fields = classOfFromObject.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			autowireDaoInTargetWithMocksFromSource(objectToAutowire, toTakeMockDaosFrom, classOfToObject, field);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void autowireDaoInTargetWithMocksFromSource(Object objectToAutowire, Object toTakeMockDaosFrom,
			Class<T> classOfToObject, Field field) throws IllegalAccessException {
		Annotation[] annotations = field.getAnnotations();
		for (int j = 0; j < annotations.length; j++) {
			Annotation annotation = annotations[j];
			if (annotation instanceof Mock) {
				Class<T> classOfField = (Class<T>) field.getType();
				if (PicsDAO.class.isAssignableFrom(classOfField) || BaseTableDAO.class.isAssignableFrom(classOfField)) {
					String fieldNameToSet = fieldNameOfAutowiredFieldOfClass(classOfField, classOfToObject);
					field.setAccessible(true);
					Object fieldValue = field.get(toTakeMockDaosFrom);
					forceSetPrivateField(classOfToObject, objectToAutowire, fieldNameToSet, fieldValue);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> String fieldNameOfAutowiredFieldOfClass(Class<T> classOfField, Class<T> classOfToObject) {
		String fieldName = null;
		List<Field> fields = new ArrayList<Field>();
		getAllFields(fields, classOfToObject);
		for (Field field : fields) {
			Annotation[] annotations = field.getAnnotations();
			for (int j = 0; j < annotations.length; j++) {
				Annotation annotation = annotations[j];
				if (annotation instanceof Autowired) {
					Class<T> classOfTargetField = (Class<T>) field.getType();
					if (classOfTargetField.isAssignableFrom(classOfField)) {
						fieldName = field.getName();
					}
				}
			}
		}
		return fieldName;
	}
	
	public static void getAllFields(List<Field> fields, Class<?> type) {
	    for (Field field: type.getDeclaredFields()) {
	        fields.add(field);
	    }
	    if (type.getSuperclass() != null) {
	         getAllFields(fields, type.getSuperclass());
	    }
	}

	
	@SuppressWarnings("unchecked")
	public <T> void autowireEMInjectedDAOs(Object objectToAutowire, EntityManager em) throws InstantiationException,
			IllegalAccessException {
		this.em = em;
		Class<T> classOfObject = (Class<T>) objectToAutowire.getClass();
		while (null != classOfObject) {
			autowireEMInjectedDAOs(objectToAutowire, classOfObject);
			classOfObject = (Class<T>) classOfObject.getSuperclass();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> void forceSetPrivateField(Object objectToForceSet, String fieldname, Object fieldValue) {
		Class<T> classOfObject = (Class<T>) objectToForceSet.getClass();
		forceSetPrivateField(classOfObject, objectToForceSet, fieldname, fieldValue);
	}

	private <T> void autowireEMInjectedDAOs(Object objectToAutowire, Class<T> classOfObject)
			throws InstantiationException, IllegalAccessException {
		Field[] fields = classOfObject.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			wireDAOAnnotations(objectToAutowire, field);
		}
	}

	private void wireDAOAnnotations(Object objectToAutowire, Field field) throws InstantiationException,
			IllegalAccessException {
		Annotation[] annotations = field.getAnnotations();
		for (int j = 0; j < annotations.length; j++) {
			Annotation annotation = annotations[j];
			wireIfDAOAnnotation(objectToAutowire, field, annotation);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void wireIfDAOAnnotation(Object objectToAutowire, Field field, Annotation annotation)
			throws InstantiationException, IllegalAccessException {
		if (annotation instanceof Autowired) {
			Class<T> classOfField = (Class<T>) field.getType();
			if (PicsDAO.class.isAssignableFrom(classOfField)) {
				PicsDAO dao = (PicsDAO) classOfField.newInstance();
				dao.setEntityManager(em);
				forceSetPrivateField(objectToAutowire, field.getName(), dao);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void forceSetPrivateField(Class<T> classOfObject, Object objectToForceSet, String fieldname, Object fieldValue) {
		try {
			Field field = classOfObject.getDeclaredField(fieldname);
			field.setAccessible(true);
			field.set(objectToForceSet, fieldValue);
		} catch (NoSuchFieldException x) {
			Class<T> superClass = (Class<T>) classOfObject.getSuperclass();
			if (null != superClass) {
				forceSetPrivateField(superClass, objectToForceSet, fieldname, fieldValue);
			}
		} catch (IllegalArgumentException x) {
			// ignore
		} catch (IllegalAccessException x) {
			// ignore
		}
	}
}
