package com.picsauditing.strutsutil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.util.CollectionUtils;

import com.picsauditing.model.i18n.KeyValue;

public class PicsRestfulActionMapperTest {

	private static final String PRIVATE_METHOD_getMethodName = "getMethodName";
	private PicsRestfulActionMapper actionMapper;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.actionMapper = new PicsRestfulActionMapper();
	}

	@Test
	public void testGetMapping_HttpGet_IndexMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("GET"), "",
				fakeParsedUrlWrapper(null));

		assertEquals("index", result);
	}

	@Test
	public void testGetMapping_HttpPost_CreateMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("POST"), "",
				fakeParsedUrlWrapper(null));

		assertEquals("insert", result);
	}

	@Test
	public void testGetMapping_HttpGet_CreateMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("GET"), "create",
				fakeParsedUrlWrapper(null));

		assertEquals("create", result);
	}

	@Test
	public void testGetMapping_HttpPost_CreateMethodReturnInsertMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("POST"),
				"create", fakeParsedUrlWrapper(null));

		assertEquals("insert", result);
	}

	@Test
	public void testGetMapping_HttpGet_IdParameter() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("GET"), "1",
				fakeParsedUrlWrapper(null));

		assertEquals("show", result);
	}

	@Test
	public void testGetMapping_HttpPut_Edit() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("PUT"), "1",
				fakeParsedUrlWrapper(null));

		assertEquals("update", result);
	}

	@Test
	public void testGetMapping_HttpPost_Edit() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("POST"), "1",
				fakeParsedUrlWrapper("edit"));

		assertEquals("update", result);
	}

	@Test
	public void testGetMapping_HttpDelete() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("DELETE"), "1",
				fakeParsedUrlWrapper(null));

		assertEquals("delete", result);
	}

	@Test
	public void testGetMapping_HttpGet_EditMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("GET"), "1",
				fakeParsedUrlWrapper("edit"));

		assertEquals("edit", result);
	}

	@Test
	public void testGetMapping_HttpGet_DynamicMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("GET"), "1",
				fakeParsedUrlWrapper("dynamicMethod"));

		assertEquals("dynamicMethod", result);
	}

	@Test
	public void testGetMapping_HttpPost_DynamicMethod() throws Exception {
		String result = Whitebox.invokeMethod(actionMapper, PRIVATE_METHOD_getMethodName, mockRequest("POST"), "1",
				fakeParsedUrlWrapper("dynamicMethod"));

		assertEquals("dynamicMethod", result);
	}

	private HttpServletRequest mockRequest(String method) {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		when(request.getMethod()).thenReturn(method);
		return request;
	}

	private ParsedUrlWrapper fakeParsedUrlWrapper(String uri) {
		ParsedUrlWrapper parsedUrlWrapper = new ParsedUrlWrapper(uri);
		parsedUrlWrapper.parse();
		return parsedUrlWrapper;
	}

	@Test
	public void testGetUriFromActionMapping_NoMethodAndNoParameters() {
		ActionMapping mapping = new ActionMapping("delete-employee", "employee", null, null);

		String result = actionMapper.getUriFromActionMapping(mapping);

		assertEquals("employee/delete-employee", result);
	}

	@Test
	public void testGetUriFromActionMapping_NoMethodAndIdParameter() {
		ActionMapping mapping = new ActionMapping("delete-employee", "employee", null, buildParams("id", 123));

		String result = actionMapper.getUriFromActionMapping(mapping);

		assertEquals("employee/delete-employee/123", result);
	}

	@Test
	public void testGetUriFromActionMapping_IncorrctMethodAndNoParameters() {
		ActionMapping mapping = new ActionMapping("delete-employee", "employee", "something", null);

		String result = actionMapper.getUriFromActionMapping(mapping);

		assertEquals("employee/delete-employee", result);
	}

	@Test
	public void testGetUriFromActionMapping_NoMethodAndMultipleParameters() {
		ActionMapping mapping = new ActionMapping("delete-employee", "employee", null, buildParams("id", 123, "blah",
				"test"));

		String result = actionMapper.getUriFromActionMapping(mapping);

		assertEquals("employee/delete-employee/123", result);
	}

	@Test
	public void testGetUriFromActionMapping_CreateMethodAndNoParameters() {
		ActionMapping mapping = new ActionMapping("delete-employee", "employee", "create", null);

		String result = actionMapper.getUriFromActionMapping(mapping);

		assertEquals("employee/delete-employee/create", result);
	}

	@Test
	public void testGetUriFromActionMapping_MethodAndMultipleParameters() {
		ActionMapping mapping = new ActionMapping("delete-employee", "employee", "customMethod", buildParams("id", 123,
				"blah", "test"));

		String result = actionMapper.getUriFromActionMapping(mapping);

		assertEquals("employee/delete-employee/123/customMethod", result);
	}

	private Map<String, Object> buildParams(Object... params) {
		if (ArrayUtils.isEmpty(params)) {
			return Collections.emptyMap();
		}

		if (params.length % 2 != 0) {
			throw new IllegalArgumentException("Parameters must follow key, value pair list");
		}

		List<KeyValue<String, Object>> keyValues = new ArrayList<>();
		for (int i = 0; i < params.length; i += 2) {
			KeyValue<String, Object> param = new KeyValue<String, Object>(params[i].toString(), params[i + 1]);
			keyValues.add(param);
		}

		return buildParams(keyValues);
	}

	private Map<String, Object> buildParams(List<KeyValue<String, Object>> params) {
		if (CollectionUtils.isEmpty(params)) {
			return Collections.emptyMap();
		}

		Map<String, Object> paramMap = new HashMap<String, Object>();
		for (KeyValue<String, Object> param : params) {
			paramMap.put(param.getKey(), param.getValue());
		}

		return paramMap;
	}
}
