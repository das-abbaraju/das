package com.picsauditing.mock;


import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.reflect.Whitebox;

import com.picsauditing.access.Permissions;
import com.picsauditing.search.SearchEngine;

public class SearchEngineMockPolicy implements PowerMockPolicy {

	@Override
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(SearchEngine.class.getName());

	}

	@Override
	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		final Method buildTerm = Whitebox.getMethod(SearchEngine.class, "buildTerm", String.class, boolean.class, boolean.class);
		settings.stubMethod(buildTerm, Collections.emptyList());
		final Method buildNativeOperatorSearch = Whitebox.getMethod(SearchEngine.class, "buildNativeOperatorSearch", Permissions.class, List.class);
		settings.stubMethod(buildNativeOperatorSearch, "");
	}

}
