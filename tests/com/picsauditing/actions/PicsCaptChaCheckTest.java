package com.picsauditing.actions;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.search.Database;
import com.picsauditing.strutsutil.AjaxUtils;
import com.picsauditing.util.SpringUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SpringUtils.class, AjaxUtils.class, I18nCache.class })
public class PicsCaptChaCheckTest {
	PicsCaptChaCheck picsCaptChaCheck;

	@Mock
	private I18nCache i18nCache;
	@Mock
	private Database databaseForTesting;
	@Mock
	private PicsActionSupport picsActionSupport;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(I18nCache.class);
		when(I18nCache.getInstance()).thenReturn(i18nCache);

		picsCaptChaCheck = new PicsCaptChaCheck();
	}

	@Test
	public void testIsPicsCaptchaResponseValid_match() {		
		assertTrue("response matches sum", picsCaptChaCheck.isPicsCaptchaResponseValid(6, 6));
	}

	@Test
	public void testIsPicsCaptchaResponseValid_notMatch() {
		assertNull("response not match sum", picsCaptChaCheck.isPicsCaptchaResponseValid(6,5));
	}

}
