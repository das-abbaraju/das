package com.picsauditing.autopatches;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AbstractAutoPatchTest {

	class Patch99Foo extends AbstractAutoPatch {
	}

	class Patch9999999 extends AbstractAutoPatch {
	}

	class Patch000123 extends AbstractAutoPatch {
	}

	@Test
	public void testGetName() {
		Patch99Foo patch = new Patch99Foo();
		assertEquals("Patch99Foo", patch.getName());
	}

	@Test
	public void testGetLevel_short_number() {
		Patch99Foo patch = new Patch99Foo();
		assertEquals(99, patch.getLevel().intValue());
	}

	@Test
	public void testGetLevel_long_number() {
		Patch9999999 patch = new Patch9999999();
		assertEquals(9999999, patch.getLevel().intValue());
	}
	@Test
	public void testGetLevel_leadingZeros() {
		Patch000123 patch = new Patch000123();
		assertEquals(123, patch.getLevel().intValue());
	}

}
