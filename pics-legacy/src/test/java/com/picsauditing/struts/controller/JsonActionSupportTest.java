package com.picsauditing.struts.controller;

import com.picsauditing.PicsActionTest;
import org.apache.struts2.ServletActionContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletActionContext.class})
public class JsonActionSupportTest extends PicsActionTest {

    private JsonActionSupport jsonActionSupport;

    private MockHttpServletRequest mockRequest;

    @Before
    public void setup() throws Exception {
        jsonActionSupport = new JsonActionSupport() {
        };
        MockitoAnnotations.initMocks(this);
        mockRequest = new MockHttpServletRequest();

        PowerMockito.mockStatic(ServletActionContext.class);
        PowerMockito.when(ServletActionContext.getRequest()).thenReturn(mockRequest);
    }

    @Test
    public void testGetModelFromJsonRequest() throws IOException {
        mockRequest.setContent("{test:'test', number: 1}".getBytes());

        TestModel modelFromJsonRequest = jsonActionSupport.getModelFromJsonRequest(TestModel.class);

        assertNotNull(modelFromJsonRequest);
        assertEquals("test", modelFromJsonRequest.getTest());
        assertEquals(1, modelFromJsonRequest.getNumber());
    }

    @Test(expected = IOException.class)
    public void testGetModelFromJsonRequest_MalformedJson() throws IOException {
        mockRequest.setContent("{test; 'test', number: 1}".getBytes());

        jsonActionSupport.getModelFromJsonRequest(TestModel.class);
    }

    @Test(expected = IOException.class)
    public void testGetModelFromJsonRequest_EmptyString() throws IOException {
        mockRequest.setContent("".getBytes());

        jsonActionSupport.getModelFromJsonRequest(TestModel.class);
    }

    @Test
    public void testGetBody() throws IOException {
        mockRequest.setContent("{test:'test', number: 2}".getBytes());

        String actualRequestBody = jsonActionSupport.getBody(mockRequest);
        assertEquals("{test:'test', number: 2}", actualRequestBody);
    }

    private class TestModel {
        private String test;
        private int number;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}