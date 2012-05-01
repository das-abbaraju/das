package com.picsauditing;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.EntityManager;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.operators.ManageJobSites;
import com.picsauditing.dao.PicsDAO;

@RunWith(PowerMockRunner.class)
@PrepareForTest(I18nCache.class)
public abstract class PicsTest {
	@Mock
	protected EntityManager em;
	@Mock
	protected I18nCache i18nCache;

	public void setUp() throws Exception {
		// MockitoAnnotations.initMocks(this);

		mockStatic(I18nCache.class);
		Mockito.when(I18nCache.getInstance()).thenReturn(i18nCache);
	}

	@SuppressWarnings("unchecked")
	protected <T> void autowireEMInjectedDAOs(Object objectToAutowire) throws InstantiationException,
			IllegalAccessException {
		Class<T> classOfObject = (Class<T>) objectToAutowire.getClass();
		while (null != classOfObject) {
			autowireEMInjectedDAOs(objectToAutowire, classOfObject);
			classOfObject = (Class<T>) classOfObject.getSuperclass();
		}
	}

	protected <T> void autowireEMInjectedDAOs(Object objectToAutowire, Class<T> classOfObject)
			throws InstantiationException, IllegalAccessException {
		Field[] fields = classOfObject.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			wireDAOAnnotations(objectToAutowire, field);
		}
	}

	protected void wireDAOAnnotations(Object objectToAutowire, Field field) throws InstantiationException,
			IllegalAccessException {
		Annotation[] annotations = field.getAnnotations();
		for (int j = 0; j < annotations.length; j++) {
			Annotation annotation = annotations[j];
			wireIfDAOAnnotation(objectToAutowire, field, annotation);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> void wireIfDAOAnnotation(Object objectToAutowire, Field field, Annotation annotation)
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
	protected <T> void forceSetPrivateField(Object objectToForceSet, String fieldname, Object fieldValue) {
		Class<T> classOfObject = (Class<T>) objectToForceSet.getClass();
		forceSetPrivateField(classOfObject, objectToForceSet, fieldname, fieldValue);
	}

	@SuppressWarnings("unchecked")
	protected <T> void forceSetPrivateField(Class<T> classOfObject, Object objectToForceSet, String fieldname,
			Object fieldValue) {
		try {
			Field field = classOfObject.getDeclaredField(fieldname);
			field.setAccessible(true);
			field.set(objectToForceSet, fieldValue);
		} catch (NoSuchFieldException x) {
			Class<T> superClass = (Class<T>) classOfObject.getSuperclass();
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
