package com.picsauditing.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.reflect.Whitebox;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import com.picsauditing.PicsActionTest;

public class URLUtilsTest extends PicsActionTest {
	private URLUtils urlUtils;

	private ActionContext originalContext;
	private ActionContext originalServletContext;

	@Mock
	private ActionContext actionContext;
	@Mock
	private ActionMapping actionMapping;
	@Mock
	private Container container;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private Map<String, Object> map;
	@Mock
	private ValueStack stack;
	@Mock
	private XWorkConverter converter;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		setupMocks();

		urlUtils = new URLUtils();

		originalContext = ActionContext.getContext();
		originalServletContext = ServletActionContext.getContext();

		ActionContext.setContext(actionContext);
		ServletActionContext.setContext(actionContext);

		when(actionContext.getContainer()).thenReturn(container);
		when(actionContext.getValueStack()).thenReturn(stack);
		when(actionMapping.getName()).thenReturn("Action");
		when(actionMapping.getNamespace()).thenReturn("/");
		when(container.getInstance(eq(String.class), anyString())).thenReturn("8080");
		when(container.getInstance(XWorkConverter.class)).thenReturn(converter);
		when(converter.convertValue(eq(map), any(), any(Class.class))).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				if (invocation.getArguments() != null && invocation.getArguments().length > 1) {
					return invocation.getArguments()[1];
				}

				return null;
			}
		});
		when(map.get(anyString())).thenReturn(container);
		when(response.encodeURL(anyString())).thenAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				return invocation.getArguments()[0];
			}
		});
		when(request.getContextPath()).thenReturn("/");
		when(stack.getContext()).thenReturn(map);

		Whitebox.setInternalState(urlUtils, "actionMapping", actionMapping);
		Whitebox.setInternalState(urlUtils, "request", request);
		Whitebox.setInternalState(urlUtils, "response", response);
	}

	@After
	public void tearDown() {
		ActionContext.setContext(originalContext);
		ServletActionContext.setContext(originalServletContext);
	}

	@Test
	public void testGetActionUrl() throws Exception {
		assertEquals("/Action.action", urlUtils.getActionUrl("Action"));

		when(actionMapping.getMethod()).thenReturn("test");
		assertEquals("/Action!test.action", urlUtils.getActionUrl("Action", "test"));

		when(actionMapping.getMethod()).thenReturn(null);
		Map<String, Object> parameters = new TreeMap<String, Object>();
		parameters.put("param1", "one");
		parameters.put("param2", 2);
		parameters.put("param3", true);

		assertEquals("/Action.action?param1=one&param2=2&param3=true", urlUtils.getActionUrl("Action", parameters));
		assertEquals("/Action.action?param1=one&param2=2&param3=true",
				urlUtils.getActionUrl("Action", "param1", "one", "param2", 2, "param3", true));
	}
}
