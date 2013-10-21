package com.picsauditing.strutsutil;

import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

public class PicsPrefixActionMapperTest {

    private PicsPrefixActionMapper picsPrefixActionMapper;

    @Mock
    private ActionMapping actionMapping;
    @Mock
    private ActionMapper restActionMapper;
    @Mock
    private ActionMapper defaultActionMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        picsPrefixActionMapper = new PicsPrefixActionMapper();

        Whitebox.setInternalState(picsPrefixActionMapper, "actionMappers", getActionMappers());

        setupMockBehaviors();
    }

    private void setupMockBehaviors() {
        when(defaultActionMapper.getUriFromActionMapping(actionMapping)).thenReturn("/Home.action");
        when(restActionMapper.getUriFromActionMapping(actionMapping)).thenReturn("/employee-guard/employees");
    }

    private Map<String, ActionMapper> getActionMappers() {
        Map<String, ActionMapper> actionMappers = new HashMap<>();
        actionMappers.put("/", defaultActionMapper);
        actionMappers.put("/employee-guard", restActionMapper);
        return actionMappers;
    }

    @Test
    public void testGetUriFromActionMapping() {
        when(actionMapping.getNamespace()).thenReturn("/employee-guard");

        String result = picsPrefixActionMapper.getUriFromActionMapping(actionMapping);

        assertEquals("/employee-guard/employees", result);
    }

    @Test
    public void testGetUriFromActionMapping_NullNamespace() {
        when(actionMapping.getNamespace()).thenReturn(null);

        String result = picsPrefixActionMapper.getUriFromActionMapping(actionMapping);

        assertEquals("/Home.action", result);
    }

    @Test
    public void testGetUriFromActionMapping_NamespaceNotFound() {
        when(actionMapping.getNamespace()).thenReturn("/invalid-namespace");

        String result = picsPrefixActionMapper.getUriFromActionMapping(actionMapping);

        assertNull(result);
    }
}
