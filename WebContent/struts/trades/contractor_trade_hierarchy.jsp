<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:property value="#node.data.name"/>
<ul class="clearfix">
<s:if test="#node.children.size() > 0">
<s:iterator value="#node.children" var="node">
	<li>
		<s:include value="contractor_trade_hierarchy.jsp"/>
	</li>
</s:iterator>
</s:if>
</ul>