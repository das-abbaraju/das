package com.picsauditing.strutsutil;

import com.google.common.base.Joiner;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.StreamResult;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.JSONTypes;
import net.sf.json.xml.XMLSerializer;

public class XMLResult extends StreamResult {
    private static final Logger logger = LoggerFactory.getLogger(XMLResult.class);
    private static final long serialVersionUID = 7789432829226367722L;

    @SuppressWarnings("unchecked")
    @Override
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("In XMLResult");
        }
        JSONObject json = (JSONObject) invocation.getStack().findValue("json");
        if (logger.isDebugEnabled()) {
            logger.debug("Have JSON object");
        }
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
        if (logger.isDebugEnabled()) {
            logger.debug("about to create xmlserializer");
        }
        net.sf.json.xml.XMLSerializer serializer = new net.sf.json.xml.XMLSerializer();
        if (logger.isDebugEnabled()) {
            logger.debug("about to create net.sf.JSON object");
        }
        net.sf.json.JSON netsfjson = net.sf.json.JSONSerializer.toJSON( json.toJSONString() );
        if (logger.isDebugEnabled()) {
            logger.debug("Configuring serializer");
        }
        serializer.setTypeHintsEnabled(false);
		serializer.setRootName("datafeed");
		serializer.setElementName("record");
        if (logger.isDebugEnabled()) {
            logger.debug("writing XML");
        }
        String xml = serializer.write( netsfjson );
        if (logger.isDebugEnabled()) {
            logger.debug("converting to inputstream");
        }
        inputStream = new ByteArrayInputStream(xml.getBytes());
        contentType = "application/xml";
        super.doExecute(finalLocation, invocation);
    }
}
