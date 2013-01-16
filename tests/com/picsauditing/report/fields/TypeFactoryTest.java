package com.picsauditing.report.fields;

import static org.junit.Assert.assertEquals;

import com.picsauditing.util.Strings;
import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;

import java.util.Set;
import java.util.TreeSet;

public class TypeFactoryTest {

	String expected = "Field\tFilter\tDisplay\tSqlFunctions\n" +
			"AccountID\tAccountID\tNumber\tCount,CountDistinct,Max,Min\n" +
			"AccountLevel\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountStatus\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountType\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountUser\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"ApprovalStatus\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditStatus\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditSubStatus\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditType\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditTypeClass\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditQuestion\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditCategory\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Boolean\tBoolean\tBoolean\tCount,CountDistinct,Max,Min\n" +
			"Contractor\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"ContractorOperatorNumberType\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Country\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"CountrySubdivision\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Currency\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Date\tDate\tString\tCount,CountDistinct,GroupConcat,Max,Min,LowerCase,UpperCase,Month,Year,YearMonth,WeekDay,Hour\n" +
			"DateTime\tDateTime\tString\tCount,CountDistinct,GroupConcat,Max,Min,LowerCase,UpperCase,Month,Year,YearMonth,WeekDay,Hour,Date\n" +
			"FlagColor\tShortList\tFlag\tCount,CountDistinct,Max,Min\n" +
			"FlagCriteriaOptionCode\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Float\tFloat\tNumber\tCount,CountDistinct,Max,Min,Average,Round,Sum,StdDev\n" +
			"Integer\tInteger\tNumber\tCount,CountDistinct,Max,Min,Average,Sum,StdDev\n" +
			"LowMedHigh\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"MultiYearScope\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"NetworkLevel\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Number\tInteger\tNumber\tCount,CountDistinct,Max,Min,Average,Round,Sum,StdDev\n" +
			"Operator\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OperatorTag\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OptionGroup\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OptionValue\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OshaRateType\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OshaType\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"PaymentMethod\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"String\tString\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Trade\tAutocomplete\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"TransactionStatus\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"UserAccountRole\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"UserID\tUserID\tNumber\tCount,CountDistinct,Max,Min\n" +
			"WaitingOn\tShortList\tString\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n";

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

		assertEquals(actual.toString(), expected);
	}
}
