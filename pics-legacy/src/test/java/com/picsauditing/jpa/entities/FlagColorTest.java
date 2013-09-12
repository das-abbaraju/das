package com.picsauditing.jpa.entities;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.PicsActionTest;

public class FlagColorTest extends PicsActionTest {

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		setupMocks();
	}

	@Test
	public void testGetSmallIcon() throws Exception {
		when(translationService.getText(eq("FlagColor.Red"), any(Locale.class))).thenReturn(new String("Red"));
		String target = "<img src=\"images/icon_redFlag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"test: Red\" />";
		assertEquals(target, FlagColor.Red.getSmallIcon("test"));
	}

	@Test
	public void testGetSmallIcon_passNull() throws Exception {
		when(translationService.getText(eq("FlagColor.Red"), any(Locale.class))).thenReturn(new String("Red"));
		String target = "<img src=\"images/icon_redFlag.gif\" width=\"10\" height=\"12\" border=\"0\" title=\"Red\" />";
		assertEquals(target, FlagColor.Red.getSmallIcon(null));
	}
}
