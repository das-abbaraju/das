<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<s:if test="contractor.lastRecalculation != null">
	<s:text name="ContractorView.LastSync"><s:param value="%{contractor.lastRecalculation}" /></s:text>
</s:if>
<pics:permission perm="DevelopmentEnvironment">
	<s:include value="../actionMessages.jsp" />
</pics:permission>