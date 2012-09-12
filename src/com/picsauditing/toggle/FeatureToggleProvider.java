package com.picsauditing.toggle;

public interface FeatureToggleProvider {
	String findFeatureToggle(String toggleName);
}
