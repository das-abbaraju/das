package com.picsauditing.actions.qa;

import org.junit.AfterClass;
import org.mockito.Mock;
import org.powermock.reflect.Whitebox;

public abstract class AnalyzerTest {
	@Mock
	QueryRunner queryRunner;

	public void setUp() throws Exception {
		Whitebox.setInternalState(QueryRunnerFactory.class, "queryRunnerBuilder", new MockQueryRunnerBuilder());
	}

	@AfterClass
	public static void teardownClass() throws Exception {
		Whitebox.setInternalState(QueryRunnerFactory.class, "queryRunnerBuilder", new TabularResultQueryRunnerBuilder());
	}

	private class MockQueryRunnerBuilder implements QueryRunnerBuilder {
		@Override
		public QueryRunner build() {
			return queryRunner;
		}
	}

}