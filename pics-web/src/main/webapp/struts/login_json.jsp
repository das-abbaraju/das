<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%
response.setContentType("text/javascript");
%>
<s:if test="callback != null">
	<s:property value="callback"/>(<s:property value="json" escape="false" /> );
</s:if>