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
td a {
	background: none !important;
}
</style>
<script type="text/javascript">
$(function() {
	wireClueTips();
});
</script>
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
				<td>Type</td>
				<td>Calculated Risk</td>
				<td>Contractor Risk</td>
				<td>Notes</td>
				<td></td>
				<td>
					<a href="#" class="cluetip help" rel="#approve_cluetip" title="Approve Risk Level">
						<img src="images/help.gif" alt="help" />
					</a>
					<div id="approve_cluetip">If approved, the highest of the Contractor selected product risk Levels will be 
						set as their new product risk level.</div>
				</td>
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
				<td><s:property value="get('riskType')" /></td>
				<td><s:property value="@com.picsauditing.jpa.entities.LowMedHigh@getName(get('risk'))" /></td>
				<td><s:property value="get('answer')" escape="false" /></td>
				<s:form action="ReportContractorRiskLevel" method="POST">
					<s:hidden value="%{get('id')}" name="conID" />
					<s:hidden value="%{get('riskType')}" name="type" />
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
