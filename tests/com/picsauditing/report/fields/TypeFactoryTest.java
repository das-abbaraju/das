package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.picsauditing.util.Strings;

public class TypeFactoryTest {

	String expected = "Field\tFilter\tDisplay\tSqlFunctions\n" +
			"AccountID\tAccountID\tNumber\tCount,CountDistinct,Max,Min\n" +
			"AccountLevel\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountStatus\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountType\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountUser\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"ApprovalStatus\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditStatus\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditSubStatus\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditType\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditTypeClass\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditQuestion\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditCategory\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Boolean\tBoolean\tBoolean\tCount,CountDistinct,Max,Min\n" +
			"Contractor\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"ContractorOperatorNumberType\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Country\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"CountrySubdivision\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Currency\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Date\tDate\tString\tCount,CountDistinct,GroupConcat,Max,Min,LowerCase,UpperCase,Month,Year,YearMonth,WeekDay,Hour\n" +
			"DateTime\tDateTime\tString\tCount,CountDistinct,GroupConcat,Max,Min,LowerCase,UpperCase,Month,Year,YearMonth,WeekDay,Hour,Date\n" +
			"FlagColor\tMultiselect\tFlag\tCount,CountDistinct,Max,Min\n" +
			"FlagCriteriaOptionCode\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Float\tNumber\tNumber\tCount,CountDistinct,Max,Min,Average,Round,Sum,StdDev\n" +
			"Integer\tNumber\tNumber\tCount,CountDistinct,Max,Min,Average,Sum,StdDev\n" +
			"LowMedHigh\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"MultiYearScope\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"NetworkLevel\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Number\tNumber\tNumber\tCount,CountDistinct,Max,Min,Average,Round,Sum,StdDev\n" +
			"Operator\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OperatorTag\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OptionGroup\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OptionValue\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OshaRateType\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OshaType\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"PaymentMethod\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"String\tString\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Trade\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"TransactionStatus\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"UserAccountRole\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"UserID\tUserID\tNumber\tCount,CountDistinct,Max,Min\n" +
			"WaitingOn\tMultiselect\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n";

	@Test
	public void testAllTypes() throws Exception {
		StringBuilder actual = new StringBuilder();
		actual.append("Field");
		actual.append("\t");
		actual.append("Filter");
		actual.append("\t");
		actual.append("Display");
		actual.append("\t");
		actual.append("SqlFunctions");
		actual.append("\n");
		for (FieldType fieldType : FieldType.values()) {
			actual.append(fieldType.toString());
			actual.append("\t");
			actual.append(fieldType.getFilterType().toString());
			actual.append("\t");
			actual.append(fieldType.getDisplayType().toString());
			actual.append("\t");
			Set<SqlFunction> sortedSqlFunctions = new TreeSet<SqlFunction>(fieldType.getSqlFunctions()) ;
			actual.append(Strings.implode(sortedSqlFunctions));
			actual.append("\n");
		}
//		Approvals.verify(actual.toString());

		assertEquals(expected, actual.toString());
	}
}
