<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<ul>
	<li class="center"><h3><s:property value="service.description"/></h3></li>
	<li><label><s:property value="service.classificationType"/></label> <s:property value="service.classificationCode"/></li>
	<s:if test="service.productI">
		<li><label>Product</label></li>
	</s:if>
	<s:if test="service.serviceI">
		<li><label>Service</label></li>
	</s:if>
	<li><label>Risk:</label> <s:property value="service.riskLevelI"/></li>
	<s:if test="service.psmApplies">
		<li><label>PSM Critical</label></li>
	</s:if>
	<s:if test="service.mappedServices.get(@com.picsauditing.jpa.entities.ClassificationType@Suncor)">
	<li><label>Suncor Children:</label>
		<ul>
		<s:iterator value="service.mappedServices.get(@com.picsauditing.jpa.entities.ClassificationType@Suncor)">
			<li><label><s:property value="classificationType"/>-<s:property value="classificationCode"/>:</label> <s:property value="description"/></li>
		</s:iterator>
		</ul>
	</li>
	</s:if>
</ul>