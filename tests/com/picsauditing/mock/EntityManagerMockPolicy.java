package com.picsauditing.mock;

import org.powermock.core.spi.PowerMockPolicy;
import org.powermock.mockpolicies.MockPolicyClassLoadingSettings;
import org.powermock.mockpolicies.MockPolicyInterceptionSettings;
import org.powermock.reflect.Whitebox;
import java.lang.reflect.Field;

import com.picsauditing.dao.PicsDAO;

public class EntityManagerMockPolicy implements PowerMockPolicy {

	@Override
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(PicsDAO.class.getName());
	}

	@Override
	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		final Field entityManagerField = Whitebox.getField(PicsDAO.class, "em");
		settings.addFieldToSuppress(entityManagerField);
	}

}
