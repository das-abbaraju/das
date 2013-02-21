package com.picsauditing.report.fields;

import java.util.Set;
import java.util.TreeSet;

import org.approvaltests.Approvals;
import org.approvaltests.reporters.DiffReporter;
import org.approvaltests.reporters.UseReporter;
import org.junit.Test;

import com.picsauditing.util.Strings;

@UseReporter(DiffReporter.class)
public class TypeFactoryTest {

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

		Approvals.verify(actual.toString());
	}
}
