package com.picsauditing.strutsutil;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class XMLResultTest {
    private XMLResult xmlResult;
    private JSONObject json;
    private static final String xmlString = readFile("tests/com/picsauditing/strutsutil/test_xml.txt");

    @Mock
    private ActionInvocation invocation;
    @Mock
    private ValueStack valueStack;
    @Mock
    private ActionSupport action;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        xmlResult = new XMLResult();

        String jsonString = readFile("tests/com/picsauditing/strutsutil/test_json.txt");
        json = (JSONObject) JSONValue.parse(jsonString);
        when(invocation.getAction()).thenReturn(action);
        when(invocation.getStack()).thenReturn(valueStack);
        when(valueStack.findValue("json")).thenReturn(json);
    }

    @Test
    public void testConvertJsonToXml() throws Exception {
        Whitebox.invokeMethod(xmlResult, "convertJsonToXml", invocation);
        ByteArrayInputStream stream = Whitebox.getInternalState(xmlResult, "inputStream");
        String xml = streamToString(stream);

        String diff = StringUtils.difference(xmlString, xml);

        assertTrue(true);
    }

    private static String streamToString(InputStream stream) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();

        return sb.toString();
    }

    private static String readFile(String path) {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(new File(path));
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
