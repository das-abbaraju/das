package com.picsauditing;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

import javax.persistence.EntityManager;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.picsauditing.PICS.I18nCache;

/**
 * Superclass for Unit Test classes that require common dependency breaking in PicsOrganizer. You are not by any
 * means required to use this class as a superclass for tests. Only use it if you need it as it uses PowerMockRunner
 * which will slow your tests down.
 * 
 * @author Galen Meurer
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(I18nCache.class)
public abstract class PicsTest {
	
	@Mock protected EntityManager em;
	@Mock protected I18nCache i18nCache;

	private PicsTestUtil testUtil = new PicsTestUtil();
	
	public void setUp() throws Exception {
		// I am pretty sure we want to do this in the subclass and not here. Not sure
		// of the effects of calling it twice. -GAM
		// MockitoAnnotations.initMocks(this);

		mockStatic(I18nCache.class);
		Mockito.when(I18nCache.getInstance()).thenReturn(i18nCache);
	}

	protected void autowireEMInjectedDAOs(Object objectToAutowire) 
			throws InstantiationException,IllegalAccessException {
		testUtil.autowireEMInjectedDAOs(objectToAutowire, em);
	}
}
