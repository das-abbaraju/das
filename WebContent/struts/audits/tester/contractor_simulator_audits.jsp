<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="audits.keySet().size() == 0">
	<div class="alert">No audits are required for this configuration.</div>
</s:if>
<s:else>
<s:iterator value="audits.keySet()" var="audit">
	<p><a href="#"
		onclick="fillCategories(<s:property value="id" />); return false;"><s:property
		value="name" /></a>
	</p>
	<div class="ruleDetail">
	<s:iterator value="audits.get(#audit)">
		<s:property value="include ? 'Include' : 'Exclude'"/> if
		<s:if test="acceptsBids">is Bid Only</s:if>
		<s:if test="question">
			<b><s:property value="question.columnHeaderOrQuestion"/></b>
			<s:property value="questionComparator"/>
			<b><s:property value="questionAnswer"/></b>
		</s:if>
		<s:if test="dependentAuditType">
			<b><s:property value="dependentAuditType.name"/></b> is <b><s:property value="dependentAuditStatus"/></b>
		</s:if>
		<s:if test="tag">
			has tag <s:property value="tag.tag"/>
		</s:if>
		<s:if test="manuallyAdded">Manually Added</s:if>
		<br />
	</s:iterator>
	</div>
</s:iterator>
<div class="info"><s:property value="audits.keySet().size()"/> audits will be included.</div>
</s:else>