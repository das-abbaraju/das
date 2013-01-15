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
			"AccountID\tAccountID\tRightAlign\tCount,CountDistinct,Max,Min\n" +
			"AccountLevel\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountStatus\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountType\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AccountUser\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"ApprovalStatus\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditStatus\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditSubStatus\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditType\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditTypeClass\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditQuestion\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"AuditCategory\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Boolean\tBoolean\tCheckMark\tCount,CountDistinct,Max,Min\n" +
			"Contractor\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"ContractorOperatorNumberType\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Country\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"CountrySubdivision\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Currency\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Date\tDate\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,LowerCase,UpperCase,Month,Year,YearMonth,WeekDay,Hour\n" +
			"DateTime\tDateTime\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,LowerCase,UpperCase,Month,Year,YearMonth,WeekDay,Hour,Date\n" +
			"FlagColor\tShortList\tFlag\tCount,CountDistinct,Max,Min\n" +
			"FlagCriteriaOptionCode\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Float\tFloat\tRightAlign\tCount,CountDistinct,Max,Min,Average,Round,Sum,StdDev\n" +
			"Integer\tInteger\tRightAlign\tCount,CountDistinct,Max,Min,Average,Sum,StdDev\n" +
			"LowMedHigh\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"MultiYearScope\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"NetworkLevel\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Number\tInteger\tRightAlign\tCount,CountDistinct,Max,Min,Average,Round,Sum,StdDev\n" +
			"Operator\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OperatorTag\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OptionGroup\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OptionValue\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OshaRateType\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"OshaType\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"PaymentMethod\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"String\tString\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"Trade\tAutocomplete\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"TransactionStatus\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"UserAccountRole\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n" +
			"UserID\tUserID\tRightAlign\tCount,CountDistinct,Max,Min\n" +
			"WaitingOn\tShortList\tLeftAlign\tCount,CountDistinct,GroupConcat,Max,Min,Length,LowerCase,UpperCase\n";

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
