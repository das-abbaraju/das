package com.picsauditing.PICS;

public interface I18nCacheBuildAware {
	void cacheBuildStarted(long epochTime);
	void cacheBuildStopped(long elapsedTime, boolean successful);
}
