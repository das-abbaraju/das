package com.picsauditing.strutsutil.actionmapper;

import com.picsauditing.strutsutil.HttpUtil;
import com.picsauditing.util.Strings;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RestMethodMappingTest {

    // REST Action methods
    private static final String REST_INDEX_METHOD = "index";
    private static final String REST_INSERT_METHOD = "insert";
    private static final String REST_CREATE_METHOD = "create";
    private static final String REST_SHOW_METHOD = "show";
    private static final String REST_UPDATE_METHOD = "update";
    private static final String REST_EDIT_METHOD = "edit";

    // ID Parameter constant
    public static final String ID_PARAMETER = "1";
    // Dynamic Method name in URL
    public static final String DYNAMIC_METHOD = "dynamicMethod";

    private RestMethodMapping methodMapping = new RestMethodMapping();

    @Test
    public void testGetMapping_HttpGet_IndexMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_GET_METHOD), Strings.EMPTY_STRING,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(REST_INDEX_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpPost_CreateMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_POST_METHOD), Strings.EMPTY_STRING,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(REST_INSERT_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpGet_CreateMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_GET_METHOD), REST_CREATE_METHOD,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(REST_CREATE_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpPost_CreateMethodReturnInsertMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_POST_METHOD), REST_CREATE_METHOD,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(REST_INSERT_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpGet_IdParameter() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_GET_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(REST_SHOW_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpPut_Edit() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_PUT_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(REST_UPDATE_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpPost_Edit() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_POST_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(REST_EDIT_METHOD), getRestMethodMapperConfig());

        assertEquals(REST_UPDATE_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpDelete() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_DELETE_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(null), getRestMethodMapperConfig());

        assertEquals(HttpUtil.HTTP_DELETE_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpGet_EditMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_GET_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(REST_EDIT_METHOD), getRestMethodMapperConfig());

        assertEquals(REST_EDIT_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpGet_DynamicMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_GET_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(DYNAMIC_METHOD), getRestMethodMapperConfig());

        assertEquals(DYNAMIC_METHOD, result);
    }

    @Test
    public void testGetMapping_HttpPost_DynamicMethod() throws Exception {
        String result = methodMapping.getMethodName(mockRequest(HttpUtil.HTTP_POST_METHOD), ID_PARAMETER,
                fakeParsedUrlWrapper(DYNAMIC_METHOD), getRestMethodMapperConfig());

        assertEquals(DYNAMIC_METHOD, result);
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

    private RestMethodMapperConfig getRestMethodMapperConfig() {
        return new RestMethodMapperConfig.Builder().deleteMethodName(HttpUtil.HTTP_DELETE_METHOD)
                .editMethodName(REST_EDIT_METHOD).getMethodName(REST_SHOW_METHOD).indexMethodName(REST_INDEX_METHOD)
                .newMethodName(REST_CREATE_METHOD).postMethodName(REST_INSERT_METHOD).putMethodName(REST_UPDATE_METHOD)
                .build();
    }
}
