<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<s:if test="contractorNotificationEmail.html">
    <s:property value="contractorNotificationEmail.body" escape="false" />
</s:if>
<s:else>
<pre>
<s:property value="contractorNotificationEmail.body" />
</pre>
</s:else>
