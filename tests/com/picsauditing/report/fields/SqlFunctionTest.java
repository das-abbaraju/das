package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SqlFunctionTest {

	@Mock
	Field field;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetDisplayType_verifyFunctionsThatAlwaysReturnASpecificDisplayType() throws Exception {
		assertEquals(DisplayType.Number, SqlFunction.Count.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.CountDistinct.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.Average.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.Round.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.Sum.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.StdDev.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.Length.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.Year.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.WeekDay.getDisplayType(field));
		assertEquals(DisplayType.Number, SqlFunction.Hour.getDisplayType(field));
        assertEquals(DisplayType.Number, SqlFunction.DaysFromNow.getDisplayType(field));

		assertEquals(DisplayType.String, SqlFunction.GroupConcat.getDisplayType(field));
        assertEquals(DisplayType.String, SqlFunction.YearMonth.getDisplayType(field));
		assertEquals(DisplayType.String, SqlFunction.Month.getDisplayType(field));
		assertEquals(DisplayType.String, SqlFunction.Date.getDisplayType(field));
	}

	@Test
	public void testGetDisplayType_verifyFunctionsThatAlwaysEchoTheInputFieldDisplayType() throws Exception {
		Field booleanField = new Field("foo", "foo", FieldType.Boolean);
		Field flagField = new Field("foo", "foo", FieldType.FlagColor);
		Field numberField = new Field("foo", "foo", FieldType.Number);
		Field stringField = new Field("foo", "foo", FieldType.String);

		assertEquals(DisplayType.Boolean, SqlFunction.Max.getDisplayType(booleanField));
		assertEquals(DisplayType.Flag, SqlFunction.Max.getDisplayType(flagField));
		assertEquals(DisplayType.Number, SqlFunction.Max.getDisplayType(numberField));
		assertEquals(DisplayType.String, SqlFunction.Max.getDisplayType(stringField));

		assertEquals(DisplayType.Boolean, SqlFunction.Min.getDisplayType(booleanField));
		assertEquals(DisplayType.Flag, SqlFunction.Min.getDisplayType(flagField));
		assertEquals(DisplayType.Number, SqlFunction.Min.getDisplayType(numberField));
		assertEquals(DisplayType.String, SqlFunction.Min.getDisplayType(stringField));

		assertEquals(DisplayType.Boolean, SqlFunction.LowerCase.getDisplayType(booleanField));
		assertEquals(DisplayType.Flag, SqlFunction.LowerCase.getDisplayType(flagField));
		assertEquals(DisplayType.Number, SqlFunction.LowerCase.getDisplayType(numberField));
		assertEquals(DisplayType.String, SqlFunction.LowerCase.getDisplayType(stringField));

		assertEquals(DisplayType.Boolean, SqlFunction.UpperCase.getDisplayType(booleanField));
		assertEquals(DisplayType.Flag, SqlFunction.UpperCase.getDisplayType(flagField));
		assertEquals(DisplayType.Number, SqlFunction.UpperCase.getDisplayType(numberField));
		assertEquals(DisplayType.String, SqlFunction.UpperCase.getDisplayType(stringField));
	}


	@Test
	public void testGetFilterType_verifyFunctionsThatAlwaysReturnASpecificFilterType() throws Exception {
		assertEquals(FilterType.Number, SqlFunction.Count.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.CountDistinct.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.Average.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.Round.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.Sum.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.StdDev.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.Length.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.Year.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.WeekDay.getFilterType(field));
		assertEquals(FilterType.Number, SqlFunction.Hour.getFilterType(field));
        assertEquals(FilterType.Number, SqlFunction.DaysFromNow.getFilterType(field));

		assertEquals(FilterType.String, SqlFunction.GroupConcat.getFilterType(field));
        assertEquals(FilterType.String, SqlFunction.YearMonth.getFilterType(field));
		assertEquals(FilterType.String, SqlFunction.Month.getFilterType(field));
		assertEquals(FilterType.String, SqlFunction.Date.getFilterType(field));
	}

	@Test
	public void testGetFilterType_verifyFunctionsThatAlwaysEchoTheInputFieldFilterType() throws Exception {
		Field booleanField = new Field("foo", "foo", FieldType.Boolean);
		Field dateField = new Field("foo", "foo", FieldType.Date);
		Field numberField = new Field("foo", "foo", FieldType.Number);
		Field stringField = new Field("foo", "foo", FieldType.String);

		assertEquals(FilterType.Boolean, SqlFunction.Max.getFilterType(booleanField));
		assertEquals(FilterType.Date, SqlFunction.Max.getFilterType(dateField));
		assertEquals(FilterType.Number, SqlFunction.Max.getFilterType(numberField));
		assertEquals(FilterType.String, SqlFunction.Max.getFilterType(stringField));

		assertEquals(FilterType.Boolean, SqlFunction.Min.getFilterType(booleanField));
		assertEquals(FilterType.Date, SqlFunction.Min.getFilterType(dateField));
		assertEquals(FilterType.Number, SqlFunction.Min.getFilterType(numberField));
		assertEquals(FilterType.String, SqlFunction.Min.getFilterType(stringField));

		assertEquals(FilterType.Boolean, SqlFunction.LowerCase.getFilterType(booleanField));
		assertEquals(FilterType.Date, SqlFunction.LowerCase.getFilterType(dateField));
		assertEquals(FilterType.Number, SqlFunction.LowerCase.getFilterType(numberField));
		assertEquals(FilterType.String, SqlFunction.LowerCase.getFilterType(stringField));

		assertEquals(FilterType.Boolean, SqlFunction.UpperCase.getFilterType(booleanField));
		assertEquals(FilterType.Date, SqlFunction.UpperCase.getFilterType(dateField));
		assertEquals(FilterType.Number, SqlFunction.UpperCase.getFilterType(numberField));
		assertEquals(FilterType.String, SqlFunction.UpperCase.getFilterType(stringField));
	}

	@Test
	public void testIsAggregate() throws Exception {
		assertEquals(true, SqlFunction.Count.isAggregate());
		assertEquals(true, SqlFunction.CountDistinct.isAggregate());
		assertEquals(true, SqlFunction.Average.isAggregate());
		assertEquals(false, SqlFunction.Round.isAggregate());
		assertEquals(true, SqlFunction.Sum.isAggregate());
		assertEquals(true, SqlFunction.StdDev.isAggregate());
		assertEquals(false, SqlFunction.Length.isAggregate());
		assertEquals(false, SqlFunction.Year.isAggregate());
		assertEquals(false, SqlFunction.YearMonth.isAggregate());
		assertEquals(false, SqlFunction.WeekDay.isAggregate());
		assertEquals(false, SqlFunction.Hour.isAggregate());

		assertEquals(true, SqlFunction.GroupConcat.isAggregate());
		assertEquals(false, SqlFunction.Month.isAggregate());
		assertEquals(false, SqlFunction.Date.isAggregate());
        assertEquals(false, SqlFunction.DaysFromNow.isAggregate());

		assertEquals(true, SqlFunction.Max.isAggregate());
		assertEquals(true, SqlFunction.Min.isAggregate());
		assertEquals(false, SqlFunction.LowerCase.isAggregate());
		assertEquals(false, SqlFunction.UpperCase.isAggregate());
	}
}
