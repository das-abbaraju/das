<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title>Report Contractor Risk</title>
<s:include value="reportHeader.jsp" />
<style type="text/css">
.red {
	color: red;
}
</style>
</head>
<body>
	<h1>Contractor Risk Assessment</h1>
	<s:form id="form1">
		<s:hidden name="filter.ajax" value="false" />
		<s:hidden name="filter.destinationAction" />
		<s:hidden name="filter.allowMailMerge" />
		<s:hidden name="showPage" value="1" />
		<s:hidden name="filter.startsWith" />
		<s:hidden name="orderBy" />
	</s:form>
	<div>
		<s:property value="report.pageLinksWithDynamicForm" escape="false" />
	</div>
	<table class="report" style="clear: none;">
		<thead>
			<tr>
				<td></td>
				<td><a href="javascript: changeOrderBy('form1','a.name');">Contractor Name</a></td>
				<td><a href="javascript: changeOrderBy('form1','a.creationDate');">Registration Date</a></td>
				<td>Calculated Risk</td>
				<td>Contractor Risk</td>
				<td>Notes</td>
				<td></td>
				<td></td>
			</tr>
		</thead>
		<s:iterator value="data" status="stat">
			<tr>
				<td class="right"><s:property value="#stat.count" /></td>
				<td>
					<a href="ContractorView.action?id=<s:property value="get('id')"/>"
						rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" class="contractorQuick"
						title="<s:property value="get('name')" />"><s:property value="get('name')" /></a>
				</td>
				<td><s:date name="get('creationDate')" format="M/d/yy" /></td>
				<td>
					Safety Risk: <b><s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('safetyRisk'))" /></b>
					<s:if test="get('materialSupplier') == 1">
						<br />Product Risk: <b><s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('productRisk'))" /></b>
					</s:if>
				</td>
				<td>
					Safety Risk: <b><s:property value="get('safetyRiskAnswer')" /></b>
					<s:if test="get('materialSupplier') == 1">
						<br />Safety Risk (Product): <b><s:property value="get('productSafetyRiskAnswer')" /></b>
						<br />Product Risk: <b><s:property value="get('productRiskAnswer')" /></b>
					</s:if>
				</td>
				<s:form action="ReportContractorRiskLevel" method="POST">
					<s:hidden value="%{get('id')}" name="conID" />
					<s:hidden value="%{get('safetyID')}" name="safetyID" />
					<s:hidden value="%{get('productSafetyID')}" name="productSafetyID" />
					<s:hidden value="%{get('productID')}" name="productID" />
					<td><s:textarea name="auditorNotes" cols="15" rows="4" /></td>
					<td>
						<s:submit method="reject" cssClass="picsbutton positive" value="%{getText('button.Reject')}" />
					</td>
					<td>
						<s:submit method="accept" cssClass="picsbutton negative" value="%{getText('button.Accept')}" />
					</td>
				</s:form>
			</tr>
		</s:iterator>
	</table>
</body>
</html>
