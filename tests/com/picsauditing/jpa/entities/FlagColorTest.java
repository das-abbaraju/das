package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import com.picsauditing.PicsActionTest;
import com.picsauditing.PICS.I18nCache;

public class FlagColorTest extends PicsActionTest {
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);			
		setupMocks();
		Whitebox.setInternalState(I18nCache.class, "INSTANCE", i18nCache);
	}
	
	@Test
	public void testGetSmallIcon() throws Exception {		
		when(i18nCache.getText(eq("FlagColor.Red"), any(Locale.class))).thenReturn(new String("Red"));
		String target = "<img src=\"images/icon_redFlag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"test: Red\" />";
		assertEquals(target, FlagColor.Red.getSmallIcon("test"));
	}

	@Test
	public void testGetSmallIcon_passNull() throws Exception {		
		when(i18nCache.getText(eq("FlagColor.Red"), any(Locale.class))).thenReturn(new String("Red"));
		String target = "<img src=\"images/icon_redFlag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"Red\" />";
		assertEquals(target, FlagColor.Red.getSmallIcon(null));
	}
}


