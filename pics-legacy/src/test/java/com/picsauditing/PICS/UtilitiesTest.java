package com.picsauditing.PICS;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.picsauditing.EntityFactory;
import com.picsauditing.jpa.entities.AuditType;

public class UtilitiesTest {

	@Test
	public void testIsEmptyArray() {
		assertTrue(Utilities.isEmptyArray(null));
	}

	@Test
	public void testEscapeHTML() throws Exception {
		assertEquals("", Utilities.escapeHTML(""));
		assertEquals("hello", Utilities.escapeHTML("hello"));
		assertEquals("&#60;hello&#62;", Utilities.escapeHTML("<hello>"));
		assertEquals("&#34;hello&#34;", Utilities.escapeHTML("\"hello\""));
		assertEquals("&#39;hello&#39;", Utilities.escapeHTML("'hello'"));
		assertEquals("hello<br/>goodbye", Utilities.escapeHTML("hello\ngoodbye"));
	}

	@Test
	public void testEscapeHTML_NotTruncated_NoExpansion() throws Exception {
		String plainFox = Utilities.escapeHTML("The quick brown fox",50);
		assertEquals("The quick brown fox", plainFox);
		assertEquals(19,plainFox.length());
	}

	@Test
	public void testEscapeHTML_Truncated_NoExpansion() throws Exception {
		String plainFox = Utilities.escapeHTML("The quick brown fox jumped over the lazy dog.",19);
		assertEquals("The quick brown fox...", plainFox);
		assertEquals(22,plainFox.length());
	}

	@Test
	public void testEscapeHTML_Truncated_Expanded() throws Exception {
		// The string is truncated before the special symbols are expanded
		String quotedFox = Utilities.escapeHTML("The 'quick' brown fox jumped over the lazy dog.",21);
		assertEquals("The &#39;quick&#39; brown fox...", quotedFox);
		assertEquals(32,quotedFox.length());
	}

	@Test
	public void testEscapeHTML_NotTruncated_Expanded() throws Exception {
		String quotedFox = Utilities.escapeHTML("The 'quick' brown fox",50);
		assertEquals("The &#39;quick&#39; brown fox", quotedFox);
		assertEquals(29,quotedFox.length());
	}

	@Test
	public void testCollectionsAreEqual_NullOrEmptyCollections() {
		Collection<String> emptyStringCollection = null;
		assertFalse(Utilities.collectionsAreEqual(emptyStringCollection, emptyStringCollection, String.CASE_INSENSITIVE_ORDER));

		emptyStringCollection = new ArrayList<String>();
		assertFalse(Utilities.collectionsAreEqual(emptyStringCollection, emptyStringCollection, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_DifferentSizedCollections() {
		Collection<String> collection1 = Arrays.asList("AB", "AC");
		Collection<String> collection2 = Arrays.asList("AB");
		assertFalse(Utilities.collectionsAreEqual(collection1, collection2, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_CollectionsDoNotHaveSameContents() {
		Collection<String> collection1 = Arrays.asList("AB", "AC");
		Collection<String> collection2 = Arrays.asList("AB", "CA");
		assertFalse(Utilities.collectionsAreEqual(collection1, collection2, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_CollectionsHaveSameContents() {
		Collection<String> collection1 = Arrays.asList("DE", "AB", "AC");
		Collection<String> collection2 = Arrays.asList("AC", "DE", "AB");
		assertTrue(Utilities.collectionsAreEqual(collection1, collection2, String.CASE_INSENSITIVE_ORDER));
	}

	@Test
	public void testCollectionsAreEqual_CollectionsHaveSameContents_EntriesImplement_Comparable() {
		Collection<Integer> collection1 = Arrays.asList(1, 2, 3);
		Collection<Integer> collection2 = Arrays.asList(3, 2, 1);
		assertTrue(Utilities.collectionsAreEqual(collection1, collection2));
	}

	@Test
	public void testGetIdsBaseTableEntities() {
		List<AuditType> audits = Arrays.asList(EntityFactory.makeAuditType(1),
				EntityFactory.makeAuditType(2), EntityFactory.makeAuditType(3));

		assertTrue(Utilities.collectionsAreEqual(Arrays.asList(1, 2, 3), Utilities.getIdsBaseTableEntities(audits)));
	}

    @Test
    public void testPrimitiveArrayToList() {
        List<Integer> result = Utilities.primitiveArrayToList(new int[] {1, 2, 3});

        assertEquals(3, result.size());
        assertTrue(Utilities.collectionsAreEqual(new ArrayList(Arrays.asList(1, 2, 3)), result));
    }

    @Test
    public void testPrimitiveArrayToList_NullArray() {
        assertTrue(Utilities.primitiveArrayToList(null).isEmpty());
    }
}
