<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<s:if test="showConfigMessage">
	<div class="alert">
		<s:text name="global.ConfigEnvironment"/>
	</div>
</s:if>
