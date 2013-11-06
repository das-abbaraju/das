package com.picsauditing.employeeguard.util;

import com.picsauditing.PicsTranslationTest;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class DateUtilTest extends PicsTranslationTest {
	@Test
	public void testExplodedToDate() throws Exception {
		Date date = DateUtil.explodedToDate(2013, 10, 23);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		assertEquals("2013-10-23", simpleDateFormat.format(date));
	}

	@Test
	public void testExplodedToDate_InvalidDate() throws Exception {
		assertNull(DateUtil.explodedToDate(0, 0, 0));
		assertNull(DateUtil.explodedToDate(0, 10, 23));
		assertNull(DateUtil.explodedToDate(2013, 0, 23));
		assertNull(DateUtil.explodedToDate(2013, 10, 0));
	}

	@Test
	public void testDateToExploded() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 2013);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);

		int[] exploded = DateUtil.dateToExploded(calendar.getTime());

		assertEquals(2013, exploded[0]);
		assertEquals(1, exploded[1]);
		assertEquals(1, exploded[2]);
	}

	@Test
	public void testDateToExploded_NullDate() throws Exception {
		int[] exploded = DateUtil.dateToExploded(null);

		assertNotNull(exploded);
		assertEquals(0, exploded.length);
	}

	@Test
	public void testDoesNotExpire() throws Exception {
		assertTrue(DateUtil.doesNotExpire(DateUtil.END_OF_TIME));

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 5000);
		assertTrue(DateUtil.doesNotExpire(calendar.getTime()));

		calendar.set(Calendar.YEAR, 2000);
		assertFalse(DateUtil.doesNotExpire(calendar.getTime()));
	}

	@Test
	public void testDoesNotExpireNullDate() throws Exception {
		assertFalse(DateUtil.doesNotExpire(null));
	}
}
