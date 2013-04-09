package com.picsauditing.strutsutil;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Joiner;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

public class XMLResult extends StreamResult {
    private static final Logger logger = LoggerFactory.getLogger(XMLResult.class);
    private static final long serialVersionUID = 7789432829226367722L;

    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        convertJsonToXml(invocation);
        super.doExecute(finalLocation, invocation);
    }

    private void convertJsonToXml(ActionInvocation invocation) throws Exception {
        JSONObject json = (JSONObject) invocation.getStack().findValue("json");
        ActionSupport action = (ActionSupport) invocation.getAction();
        if (action.hasActionMessages() || action.hasActionErrors()) {
            if (action.hasActionMessages())
                json.put("actionMessage", Joiner.on("\n").join(action.getActionMessages()));
            if (action.hasActionErrors())
                json.put("actionError", Joiner.on("\n").join(action.getActionErrors()));
        }

        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setAnnotationIntrospector(new MyJacksonXmlAnnotationIntrospector());
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + xmlMapper.writeValueAsString(json);

        inputStream = new ByteArrayInputStream(xml.getBytes());
        contentType = "application/xml";
    }

    private class MyJacksonXmlAnnotationIntrospector extends JacksonXmlAnnotationIntrospector {
        @Override
        public PropertyName findRootName(AnnotatedClass ac) {
            return new PropertyName("datafeed", "");
        }

    }
}
