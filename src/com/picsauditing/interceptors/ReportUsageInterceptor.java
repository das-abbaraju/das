package com.picsauditing.interceptors;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.picsauditing.access.Permissions;
import com.picsauditing.actions.report.ReportActionSupport;

public class ReportUsageInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 1L;

	private static final Matcher matcher = Pattern.compile(".+?([^\\.]+)@.+").matcher("");

	private static final String[] nonReportActionSupportClasses = {
	};

	private static final List<String> classList = Arrays.asList(nonReportActionSupportClasses);

	private static final Logger logger = LoggerFactory.getLogger(ReportUsageInterceptor.class);

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		try {
			Permissions permissions = (Permissions) ActionContext.getContext().getSession().get("permissions");
			int userId = permissions.getUserId();

			Action action = (Action) invocation.getAction();
			matcher.reset(action.toString());

			if (matcher.matches() && (action instanceof ReportActionSupport) || classList.contains(matcher.group(1))) {
				int numParameters = invocation.getInvocationContext().getParameters().keySet().size();
				Object[] logObjects = { userId, matcher.group(1), numParameters };
				logger.info("{},{},{}", logObjects);
			}
		} catch (Exception e) {
			// We're just logging report usage, nothing critical
		}

		return invocation.invoke();
	}
}
