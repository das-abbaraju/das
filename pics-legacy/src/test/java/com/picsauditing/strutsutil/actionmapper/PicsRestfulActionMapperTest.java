package com.picsauditing.strutsutil.actionmapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.strutsutil.actionmapper.ParsedUrlWrapper;
import com.picsauditing.strutsutil.actionmapper.PicsRestfulActionMapper;
import com.picsauditing.util.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.springframework.util.CollectionUtils;

import com.picsauditing.model.i18n.KeyValue;

public class PicsRestfulActionMapperTest {

	private PicsRestfulActionMapper actionMapper;

    @Mock
    private HttpServletRequest request;
    @Mock
    private Configuration configuration;
    @Mock
    private ConfigurationManager configurationManager;

    @Before
	public void setUp() throws Exception {
        this.actionMapper = new PicsRestfulActionMapper();
        actionMapper.setSlashesInActionNames("true");

		MockitoAnnotations.initMocks(this);

        buildConfigurationManager();
	}

    @Test(expected = IllegalStateException.class)
    public void testGetMapping_Error_WhenNotConfiguredToAllowSlashesInActionNames() {
        actionMapper.setSlashesInActionNames("false");

        actionMapper.getMapping(request, configurationManager);
    }

    @Test
    public void testGetMapping_GetRequest_NoId_ReturnIndexMethod() {
        setupRequest("/employee-guard/operator/dashboard", "GET");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "index", "dashboard", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("id", Strings.EMPTY_STRING);
        }});
    }

    @Test
    public void testGetMapping_GetRequest_WithId_ReturnShowMethod() {
        setupRequest("/employee-guard/operator/role/123", "GET");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "show", "role", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("id", "123");
        }});
    }

    @Test
    public void testGetMapping_GetRequest_MultipleDynamicParameters() {
        setupRequest("/employee-guard/operator/project/123/role/456", "GET");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "show", "project/{projectId}/role/{id}", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("projectId", "123");
            put("id", "456");
        }});
    }

    @Test
    public void testGetMapping_PostRequest_MultipleDynamicParameters_ReturnUpdateMethod() {
        setupRequest("/employee-guard/operator/project/123/role/456/assignment/789/edit", "POST");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "update", "project/{projectId}/role/{roleId}/assignment/{id}", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("projectId", "123");
            put("roleId", "456");
            put("id", "789");
        }});
    }

    @Test
    public void testGetMapping_PostRequest_MultipleDynamicParameters_ReturnDynamicMethod() {
        setupRequest("/employee-guard/operator/project/123/role/456/assignment/789/dynamicMethod", "POST");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "dynamicMethod", "project/{projectId}/role/{roleId}/assignment/{id}", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("projectId", "123");
            put("roleId", "456");
            put("id", "789");
        }});
    }

    @Test
    public void testGetMapping_GetRequest_MultipleDynamicParameters_PreDefinedMethodInConfiguration() {
        setupRequest("/employee-guard/operator/project/123/role/456/assignment/789/method1", "GET");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "method1", "project/{projectId}/role/{roleId}/assignment/{id}/method1", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("projectId", "123");
            put("roleId", "456");
            put("id", "789");
        }});
    }

    @Test
    public void testGetMapping_GetRequest_MultipleDynamicParameters_SecondPreDefinedMethodInConfiguration() {
        setupRequest("/employee-guard/operator/project/123/role/456/assignment/789/method2", "GET");

        ActionMapping result = actionMapper.getMapping(request, configurationManager);

        verifyActionMapping(result, "method2", "project/{projectId}/role/{roleId}/assignment/{id}/method2", "/employee-guard/operator", new HashMap<String, Object>() {{
            put("projectId", "123");
            put("roleId", "456");
            put("id", "789");
        }});
    }

    private void verifyActionMapping(ActionMapping result, String method, String name, String namespace,
                                     Map<String, Object> params) {
        assertEquals(method, result.getMethod());
        assertEquals(name, result.getName());
        assertEquals(namespace, result.getNamespace());
        assertTrue(Utilities.mapsAreEqual(params, result.getParams()));
    }

    private void setupRequest(String uri, String method) {
        when(request.getAttribute("javax.servlet.include.servlet_path")).thenReturn(uri);
        when(request.getMethod()).thenReturn(method);
    }

    private void buildConfigurationManager() {
        Map<String, ActionConfig> actionConfigs = getActionConfigs();
        PackageConfig packageConfig = getPackageConfig(actionConfigs);
        Map<String, PackageConfig> packageConfigs = new HashMap<>();
        packageConfigs.put("employee-guard/operator", packageConfig);

        when(configurationManager.getConfiguration()).thenReturn(configuration);
        when(configuration.getPackageConfigs()).thenReturn(packageConfigs);
    }

    private Map<String, ActionConfig> getActionConfigs() {
        Map<String, ActionConfig> actionConfigs = new LinkedHashMap<>(); // same Map implementation as Struts

        ActionConfigToFake configToFake = new ActionConfigToFake("/employee-guard/operator",
                "project/{projectId}/role/{roleId}/assignment/{id}/method1", "com.picsauditing.employeeguard.controllers.operator.ProjectAction");
        configToFake.setMethodName("method1");
        actionConfigs.put("project/{projectId}/role/{roleId}/assignment/{id}/method1", configToFake);

        configToFake = new ActionConfigToFake("/employee-guard/operator",
                "project/{projectId}/role/{roleId}/assignment/{id}/method2", "com.picsauditing.employeeguard.controllers.operator.ProjectAction");
        configToFake.setMethodName("method2");
        actionConfigs.put("project/{projectId}/role/{roleId}/assignment/{id}/method2", configToFake);

        actionConfigs.put("project/{projectId}/role/{roleId}/assignment/{id}", new ActionConfigToFake("/employee-guard/operator",
                "project/{projectId}/role/{roleId}/assignment/{id}", "com.picsauditing.employeeguard.controllers.operator.ProjectAction"));

        actionConfigs.put("dashboard", new ActionConfigToFake("/employee-guard/operator", "dashboard",
                "com.picsauditing.employeeguard.controllers.operator.DashboardAction"));
        actionConfigs.put("project/{projectId}/role/{id}", new ActionConfigToFake("/employee-guard/operator",
                "project/{projectId}/role/{id}", "com.picsauditing.employeeguard.controllers.operator.ProjectAction"));
        actionConfigs.put("role", new ActionConfigToFake("/employee-guard/operator", "role",
                "com.picsauditing.employeeguard.controllers.operator.RoleAction"));

        return actionConfigs;
    }

    private PackageConfig getPackageConfig(Map<String, ActionConfig> actionConfigs) {
        PackageConfigToFake packageConfigToFake = new PackageConfigToFake();
        packageConfigToFake.setNamespace("/employee-guard/operator");
        packageConfigToFake.setActionConfigs(actionConfigs);
        return packageConfigToFake;
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

    @Test
    public void testGetUriFromActionMapping_Method_MultipleUrlParameters_() {
        ActionMapping mapping = new ActionMapping("project/{projectId}/role/{id}", "/employee-guard/operator",
                "customMethod",
                buildParams("id", 123, "projectId", 456));

        String result = actionMapper.getUriFromActionMapping(mapping);

        assertEquals("/employee-guard/operator/project/456/role/123/customMethod", result);
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
			KeyValue<String, Object> param = new KeyValue<>(params[i].toString(), params[i + 1]);
			keyValues.add(param);
		}

		return buildParams(keyValues);
	}

	private Map<String, Object> buildParams(List<KeyValue<String, Object>> params) {
		if (CollectionUtils.isEmpty(params)) {
			return Collections.emptyMap();
		}

		Map<String, Object> paramMap = new HashMap<>();
		for (KeyValue<String, Object> param : params) {
			paramMap.put(param.getKey(), param.getValue());
		}

		return paramMap;
	}
}
