<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<s:if test="preview.html">
	<s:property value="preview.body" escape="false" />
</s:if>
<s:else>
<pre>
<s:property value="preview.body" />
</pre>
</s:else>
