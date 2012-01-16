<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<h3><s:property value="auditType.name" /></h3>
<ul class="categories">
	<s:iterator value="categories">
		<s:include value="contractor_simulator_category_include.jsp" />
	</s:iterator>
</ul>
