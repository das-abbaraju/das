package com.picsauditing.strutsutil;

import com.google.common.base.Joiner;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * Custom result used to return XML to the browser using the proper
 * contentType.
 *
 * @author pschlesinger
 */
public class XMLResult extends StreamResult {
    private static final Logger logger = LoggerFactory.getLogger(XMLResult.class);
    private static final long serialVersionUID = 7789432829226367722L;

    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        logger.debug("In XMLResult");
        JSONObject json = (JSONObject) invocation.getStack().findValue("json");

        /*
           * Add in the action messages/errors if there are any.
           */
        ActionSupport action = (ActionSupport) invocation.getAction();
        if (action.hasActionMessages() || action.hasActionErrors()) {
            if (action.hasActionMessages())
                json.put("actionMessage", Joiner.on("\n").join(action.getActionMessages()));
            if (action.hasActionErrors())
                json.put("actionError", Joiner.on("\n").join(action.getActionErrors()));
        }

        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        Object xmlObject = xstream.fromXML(json.toJSONString());
        String xml = xstream.toXML(xmlObject);
        inputStream = new ByteArrayInputStream(xml.getBytes());
        contentType = "application/xml";
        super.doExecute(finalLocation, invocation);
    }
}
