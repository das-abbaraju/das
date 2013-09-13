package com.picsauditing.actions.qa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.picsauditing.util.Strings;

public class TabularDataTest {
	private TabularData tabularData;
	private List<String> columnNames = Arrays.asList("foo","bar","baz","quuz");	
	private String[][] data = {
			{"Sparrow","Canary","Robin","Chickadee"},
			{"Eagle", "Hawk", "Peacock", "Osterich"}
			};
	private Integer[][] data2 = {
			{100, 101, 102, 103},
			{200, 201, 202, 203}
			};
	private Object[][] dataMixedClass = {
			{"Sparrow", 101, new Date(), Boolean.TRUE},
			{"Eagle", 201, new Date(), Boolean.FALSE}
			};
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		tabularData = new TabularData(columnNames);
	}
	
	@Test
	public void testGetColumnCount_EmptyColumns() throws Exception {
		tabularData.setColumnNames(null);
		
		assertEquals(0, tabularData.getColumnCount());
	}
	
	@Test
	public void testGetColumnCount_WithColumns() throws Exception {
		assertEquals(columnNames.size(), tabularData.getColumnCount());
	}

	@Test
	public void testGetColumnName_EmptyColumns() throws Exception {
		tabularData.setColumnNames(null);
		
		assertTrue(Strings.isEmpty(tabularData.getColumnName(1)));
	}

	@Test
	public void testGetColumnName_IndexExceedsColumns() throws Exception {
		int tooMany = columnNames.size() + 1; 
		
		assertTrue(Strings.isEmpty(tabularData.getColumnName(tooMany)));
	}
	
	@Test
	public void testSetValueAt_Sparse() throws Exception {
		tabularData.setValueAt("Sparrow", 5, 4);
		
		assertEquals("Sparrow", tabularData.getValueAt(5,4));
		assertFalse("Eagle".equals(tabularData.getValueAt(5,4)));
	}

	@Test
	public void testGetValueAt_NonExistentValue() throws Exception {
		assertNull(tabularData.getValueAt(5, 4));
	}
	
	@Test
	public void testGetValueAt_AllTestData() throws Exception {
		populateWithTestData();
		
		for (int i = 0; i < data.length; i++) {
			String[] row = data[i];
			for (int j = 0; j < row.length; j++) {
				assertEquals(data[i][j], tabularData.getValueAt(i+1,j+1));
			}
		}
	}
	
	@Test
	public void testGetRowCount_NoData() throws Exception {
		assertEquals(0, tabularData.getRowCount());
	}

	@Test
	public void testGetRowCount_WithData() throws Exception {
		populateWithTestData();
		
		assertEquals(data.length, tabularData.getRowCount());
	}

	@Test
	public void testGetData() {
		populateWithTestData();
		
		SortedMap<Integer, SortedMap<Integer,Object>> tabData = tabularData.getData();
		
		int columnCount = 0;
		for(SortedMap<Integer,Object> row : tabData.values()) {
			int rowCount = 0;
			for (Object datum : row.values()) {	
				assertEquals(data[columnCount][rowCount], datum);
				rowCount++;
			}
			columnCount++;
		}
	}
	
	@Test
	public void testGetRows() {
		populateWithTestData();
		
		Collection<SortedMap<Integer, Object>> tabData = tabularData.getRows();
		
		int columnCount = 0;
		for(SortedMap<Integer,Object> row : tabData) {
			int rowCount = 0;
			for (Object datum : row.values()) {	
				assertEquals(data[columnCount][rowCount], datum);
				rowCount++;
			}
			columnCount++;
		}
	}
	
	private void populateWithTestData() {
		populateWithTestData(data);
	}
	
	private void populateWithTestData(Object[][] data) {
		for (int i = 0; i < data.length; i++) {
			Object[] row = data[i];
			for (int j = 0; j < row.length; j++) {
				// our arrays are zero based but we want our tabular data to be 1 based column/row
				tabularData.setValueAt(data[i][j], i+1, j+1);
			}
		}
	}

	@Test
	public void testGetColumnClass() throws Exception {
		populateWithTestData(data);
		
		assertTrue(String.class.isAssignableFrom(tabularData.getColumnClass(1)));
		
		populateWithTestData(data2);
		
		assertTrue(Integer.class.isAssignableFrom(tabularData.getColumnClass(1)));
	}

	@Test
	public void testGetColumnClass_MixedClass() throws Exception {
		// 			{"Sparrow", 101, new Date(), Boolean.TRUE},
		populateWithTestData(dataMixedClass);
		
		assertTrue(String.class.isAssignableFrom(tabularData.getColumnClass(1)));
		assertTrue(Integer.class.isAssignableFrom(tabularData.getColumnClass(2)));
		assertTrue(Date.class.isAssignableFrom(tabularData.getColumnClass(3)));
		assertTrue(Boolean.class.isAssignableFrom(tabularData.getColumnClass(4)));
	}

}
