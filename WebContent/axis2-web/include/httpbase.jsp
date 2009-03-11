<%--
  ~ Licensed to the Apache Software Foundation (ASF) under one
  ~ or more contributor license agreements. See the NOTICE file
  ~ distributed with this work for additional information
  ~ regarding copyright ownership. The ASF licenses this file
  ~ to you under the Apache License, Version 2.0 (the
  ~ "License"); you may not use this file except in compliance
  ~ with the License. You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing,
  ~ software distributed under the License is distributed on an
  ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  ~ KIND, either express or implied. See the License for the
  ~ specific language governing permissions and limitations
  ~ under the License.
  --%>

<%@ page import="org.apache.axis2.AxisFault" %>
<%@ page import="org.apache.axis2.Constants" %>
<%@ page import="org.apache.axis2.addressing.EndpointReference" %>
<%@ page import="org.apache.axis2.client.Options" %>
<%@ page import="org.apache.axis2.client.ServiceClient" %>
<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.apache.axis2.context.ConfigurationContextFactory" %>
<%@ page import="org.apache.axis2.context.ServiceContext" %>
<%@ page import="org.apache.axis2.context.ServiceGroupContext" %>
<%@ page import="org.apache.axis2.deployment.util.PhasesInfo" %>
<%@ page import="org.apache.axis2.description.AxisModule" %>
<%@ page import="org.apache.axis2.description.AxisOperation" %>
<%@ page import="org.apache.axis2.description.AxisService" %>
<%@ page import="org.apache.axis2.description.AxisServiceGroup" %>
<%@ page import="org.apache.axis2.description.Parameter" %>
<%@ page import="org.apache.axis2.engine.AxisConfiguration" %>
<%@ page import="org.apache.axis2.engine.Handler" %>
<%@ page import="org.apache.axis2.engine.Phase" %>
<%@ page import="org.apache.axis2.transport.http.AxisServlet" %>
<%@ page import="org.apache.axis2.util.JavaUtils" %>
<%@ page import="javax.xml.parsers.SAXParser" %>
<%@ page import="javax.xml.parsers.SAXParserFactory" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.io.StringWriter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%!
  private String frontendHostUrl;

  public void jspInit() {
    ServletContext context = this.getServletConfig().getServletContext();
    ConfigurationContext configctx = (ConfigurationContext) context.getAttribute(AxisServlet.CONFIGURATION_CONTEXT);
    if (configctx != null){
        Parameter parameter = configctx.getAxisConfiguration().getParameter(Constants.HTTP_FRONTEND_HOST_URL);
        if (parameter != null) {
          frontendHostUrl = (String) parameter.getValue();
        }
    }
  }

  public String calculateHttpBase(HttpServletRequest aRequest) {
    StringBuffer stringBuffer = new StringBuffer();
    if (frontendHostUrl != null) {
      stringBuffer.append(frontendHostUrl);
    } else {
      String scheme = aRequest.getScheme();
      stringBuffer.append(scheme);
      stringBuffer.append("://");
      stringBuffer.append(aRequest.getServerName());
      if (("http".equalsIgnoreCase(scheme) && aRequest.getServerPort() != 80) || "https".equalsIgnoreCase(scheme) && aRequest.getServerPort() != 443)
      {
        stringBuffer.append(":");
        stringBuffer.append(aRequest.getServerPort());
      }
      // I think i saw web containers return null for root web context
      if (aRequest.getContextPath() != null) {
        stringBuffer.append(aRequest.getContextPath());
      }
    }
    // append / char if needed
    if (stringBuffer.charAt(stringBuffer.length() - 1) != '/') {
      stringBuffer.append("/");
    }
    String curentUrl = stringBuffer.toString();
    aRequest.setAttribute("frontendHostUrl", curentUrl);
    return curentUrl;
  }
%><base href="<%= calculateHttpBase(request)%>"/>