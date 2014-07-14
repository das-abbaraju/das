package com.picsauditing.employeeguard;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

public class ResourceBundleMocking {

	@Mock
	private ThreadLocal<ActionContext> threadLocalActionContext;
	@Mock
	private ActionContext actionContext;
	@Mock
	private ActionInvocation actionInvocation;
	@Mock
	private ActionSupport actionSupport;
	@Mock
	private ValueStack valueStack;

	public static final String DEFAULT_RESOURCE_BUNDLE_STRING = "DEFAULT RESOURCE BUNDLE STRING";

	public void setUp() {
		MockitoAnnotations.initMocks(this);

		initResourceBundleMocking();
	}

	private void initResourceBundleMocking() {
		Whitebox.setInternalState(ActionContext.class, threadLocalActionContext);
		when(threadLocalActionContext.get()).thenReturn(actionContext);
		when(actionContext.getActionInvocation()).thenReturn(actionInvocation);
		when(actionInvocation.getAction()).thenReturn(actionSupport);
		when(actionContext.getValueStack()).thenReturn(valueStack);
		when(actionSupport.getText(any(String.class), any(String.class), anyList(), any(ValueStack.class)))
				.thenReturn(DEFAULT_RESOURCE_BUNDLE_STRING);
	}

	public void tearDown() {
		Whitebox.setInternalState(ActionContext.class, "actionContext", new ThreadLocal<ActionContext>());
	}
}
