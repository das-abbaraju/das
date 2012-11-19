package com.picsauditing.strutsutil;

import com.google.common.base.Joiner;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.thoughtworks.xstream.XStream;
import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONObject;

import java.io.ByteArrayInputStream;

/**
 * Custom result used to return JSON to the browser using the proper
 * contentType.
 *
 * @author kpartridge
 */
public class XMLResult extends StreamResult {

    private static final long serialVersionUID = 7789432829226367722L;

    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

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

        XStream xstream = new XStream();
        String xml = xstream.toXML(json);
        inputStream = new ByteArrayInputStream(xml.getBytes());
        contentType = "application/xml";
        super.doExecute(finalLocation, invocation);
    }
}
