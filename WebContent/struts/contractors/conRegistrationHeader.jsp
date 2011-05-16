<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="permissions.contractor && !registrationStep.done">
	<s:include value="registrationHeader.jsp"></s:include>
</s:if>
<s:else>
	<s:include value="conHeader.jsp"></s:include>
</s:else>
