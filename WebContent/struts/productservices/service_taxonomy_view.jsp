<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<h4><s:property value="service.description"/></h4>
<hr />
<p><label><s:property value="service.classificationType"/> ID:</label> <s:property value="service.id"/></p>
<s:if test="service.productI">
	<p><label>Product Critical:</label> <s:property value="service.riskLevelI"/></p>
</s:if>
<s:if test="service.serviceI">
	<p><label>Safety Critical:</label> <s:property value="service.riskLevelI"/></p>
</s:if>
<s:if test="service.psmApplies">
	<p><label>PSM Critical:</label> Yes</p>
</s:if>
<s:if test="service.classificationCode.length() > 0">
	<p><label>NAICS Code:</label> <s:property value="service.classificationCode"/></p>
</s:if>
<s:if test="service.bestMatch != null">
	<p><label>Mapped To:</label> <s:property value="service.bestMatch.toString()"/></p>
</s:if>
<s:if test="service.matches.size() > 0">
<p><label>Suncor Mapping:</label>
	<ul>
	<s:iterator value="service.matches" var="suncor">
		<li><s:property value="#suncor.toString()"/> (<s:property value="#suncor.id"/>)</li>
	</s:iterator>
	</ul>
</p>
</s:if>
