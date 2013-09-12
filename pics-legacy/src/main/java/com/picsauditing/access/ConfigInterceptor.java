package com.picsauditing.access;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.util.ReflectUtil;

/**
 * Interceptor for preventing actions that can only be executed in the configuration environment.
 * 
 * @author kpartridge
 * 
 */
@SuppressWarnings("serial")
public class ConfigInterceptor extends AbstractInterceptor {

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		PicsActionSupport action = null;
		if (invocation.getAction() instanceof PicsActionSupport)
			action = (PicsActionSupport) invocation.getAction();

		if (action != null) {

			// Check if the declaring class has a config option.
			Config classConfig = ReflectUtil.getApplicableClassLevelAnnotation(action.getClass(), Config.class);

			// Check if the declaring method has a config option.
			Config methodConfig = ReflectUtil.getApplicableMethodLevelAnnotation(action.getClass(), invocation
					.getProxy().getMethod(), Config.class);

			// We should use the method level annotation if it exists, otherwise we will use the class level.
			Config config = null;
			if (methodConfig != null)
				config = methodConfig;
			else
				config = classConfig;

			if (config != null) {
				if (config.requiresConfig() && !isConfigurationEnvironment()) {
					throw new RuntimeException("Configuration is not allowed in this environment.");
				}
			}
		}

		return invocation.invoke();
	}

	private boolean isConfigurationEnvironment() {
		return "1".equals(System.getProperty("pics.config"));
	}

}
