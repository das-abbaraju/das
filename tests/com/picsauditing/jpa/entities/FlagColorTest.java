package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PICS.I18nCache;
import com.picsauditing.actions.TranslationActionSupport;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FlagColor.class, I18nCache.class, TranslationActionSupport.class })
@PowerMockIgnore({ "javax.xml.parsers.*", "ch.qos.logback.*", "org.slf4j.*", "org.apache.xerces.*" })
public class FlagColorTest {
	FlagColor flagColor;
	
	@Mock
	private I18nCache i18nCache;

	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);			
		PowerMockito.mockStatic(I18nCache.class);
	}
	
	@Test
	public void testGetSmallIcon() throws Exception {		
		PowerMockito.mockStatic(TranslationActionSupport.class);

		flagColor = FlagColor.Red;				
		when(I18nCache.getInstance()).thenReturn(i18nCache);
		when(TranslationActionSupport.getLocaleStatic()).thenReturn(Locale.ENGLISH);
		when(i18nCache.getText(anyString(), any(Locale.class), any())).thenReturn(new String("Red"));		
		String target="<img src=\"images/icon_redFlag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"test: null\" />";
		assertEquals(target, flagColor.getSmallIcon("test"));
	}

	@Test
	public void testGetSmallIcon_passNull() throws Exception {		
		PowerMockito.mockStatic(TranslationActionSupport.class);

		flagColor = FlagColor.Red;				
		when(I18nCache.getInstance()).thenReturn(i18nCache);
		when(TranslationActionSupport.getLocaleStatic()).thenReturn(Locale.ENGLISH);
		when(i18nCache.getText(anyString(), any(Locale.class), any())).thenReturn(new String("Red"));		
		String target="<img src=\"images/icon_redFlag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"null\" />";
		assertEquals(target, flagColor.getSmallIcon(null));
	}


}


