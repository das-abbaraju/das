<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Flag Status for <s:property value="contractor.name" /></title>
<script type="text/javascript" src="js/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<style>
.Green {
	color: #272;
}

.Amber {
	color: #770;
}

.Red {
	color: #900;
}
</style>
</head>
<body>
<h1><s:property value="contractor.name" /> <span class="sub">Flag
Status at <s:property value="co.operatorAccount.name" /></span></h1>
<s:push value="#request.current='Flag'" />
<s:include value="con_nav.jsp" />

<div style="text-align: center; width: 100%">
<table border="1">
	<tr>
		<td rowspan="2" style="vertical-align: middle;"><s:property
			value="co.flag.flagColor.bigIcon" escape="false" /></td>
		<td><b>Overall Flag Status</b></td>
	</tr>
	<tr>
		<td>
		<div id="override" style="display: none">asdf</div>
		<a href="#" onclick="show($('override')); return false;">Manually
		Override Color</a></td>
	</tr>
</table>
</div>

<table class="report">
	<thead>
		<tr>
			<td>Flag</td>
			<td>Required Audit</td>
		</tr>
	</thead>
	<s:iterator value="co.operatorAccount.audits">
		<s:if test="requiredForFlag && requiredForFlag.name() in {'Red', 'Amber'}">  
			<tr class="<s:property value="contractorFlag" />">
				<td class="center"><s:property value="contractorFlag.smallIcon"
					escape="false" /></td>
				<td><s:property value="auditType.auditName" /></td>
			</tr>
		</s:if>
	</s:iterator>
</table>

<pics:permission perm="ManageOperators">
	<div><a
		href="AuditOperator.action?oID=<s:property value="opID" />">Flag
	Criteria for Audits</a></div>
</pics:permission>

<s:if test="contractor.oshas.size > 0">
	<table class="report">
		<thead>
			<tr>
				<td>Flag</td>
				<td>Year</td>
				<s:if test="oshaFatalitiesUsed">
					<td>Fatalities</td>
				</s:if>
				<s:if test="oshaLwcrUsed">
					<td>LWCR</td>
				</s:if>
				<s:if test="oshaTrirUsed">
					<td>TRIR</td>
				</s:if>
			</tr>
		</thead>
		<s:iterator value="contractor.oshas">
			<s:if test="corporate">
				<tr class="<s:property value="year1.flagColor" />">
					<td class="center"><s:property
						value="year1.flagColor.smallIcon" escape="false" /></td>
					<td>2007</td>
					<s:if test="oshaFatalitiesUsed">
						<td class="right"><s:property value="year1.fatalities" /></td>
					</s:if>
					<s:if test="oshaLwcrUsed">
						<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(year1.lostWorkCasesRate)}" /></td>
					</s:if>
					<s:if test="oshaTrirUsed">
						<td class="right"><s:property
							value="%{new java.text.DecimalFormat('#,##0.0').format(year1.recordableTotalRate)}" /></td>
					</s:if>
				</tr>
				<tr class="<s:property value="year2.flagColor" />">
					<td class="center"><s:property
						value="year2.flagColor.smallIcon" escape="false" /></td>
					<td>2006</td>
					<s:if test="oshaFatalitiesUsed">
						<td class="right"><s:property value="year2.fatalities" /></td>
					</s:if>
					<s:if test="oshaLwcrUsed">
						<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(year2.lostWorkCasesRate)}" /></td>
					</s:if>
					<s:if test="oshaTrirUsed">
						<td class="right"><s:property
							value="%{new java.text.DecimalFormat('#,##0.0').format(year2.recordableTotalRate)}" /></td>
					</s:if>
				</tr>
				<tr class="<s:property value="year3.flagColor" />">
					<td class="center"><s:property
						value="year3.flagColor.smallIcon" escape="false" /></td>
					<td>2005</td>
					<s:if test="oshaFatalitiesUsed">
						<td class="right"><s:property value="year3.fatalities" /></td>
					</s:if>
					<s:if test="oshaLwcrUsed">
						<td class="right"><s:property value="%{new java.text.DecimalFormat('#,##0.0').format(year3.lostWorkCasesRate)}" /></td>
					</s:if>
					<s:if test="oshaTrirUsed">
						<td class="right"><s:property
							value="%{new java.text.DecimalFormat('#,##0.0').format(year3.recordableTotalRate)}" /></td>
					</s:if>
				</tr>
				<s:if test="oshaAveragesUsed">
					<tr class="<s:property value="flagColor" />">
						<td class="center"><s:property value="flagColor.smallIcon"
							escape="false" /></td>
						<td>Avg</td>
						<s:if test="oshaFatalitiesUsed">
							<td class="right"><s:property value="averageFatalities" /></td>
						</s:if>
						<s:if test="oshaLwcrUsed">
							<td class="right"><s:property value="averageLwcr" /></td>
						</s:if>
						<s:if test="oshaTrirUsed">
							<td class="right"><s:property value="averageTrir" /></td>
						</s:if>
					</tr>
				</s:if>
			</s:if>
		</s:iterator>
	</table>
</s:if>

<s:if test="auditData.size > 0">
	<table class="report">
		<thead>
			<tr>
				<td>Flag</td>
				<td>Answer</td>
				<td>Audit Question</td>
			</tr>
		</thead>
		<s:iterator value="auditData">
			<tr class="<s:property value="value.flagColor" />">
				<td class="center"><s:property
					value="value.flagColor.smallIcon" escape="false" /></td>
				<td class="center"><s:property value="value.answer" /></td>
				<td><s:property value="value.question.question" /></td>
			</tr>
		</s:iterator>
	</table>
</s:if>

<div><a href="ConAuditList.action?id=<s:property value="id"/>">View
Audits</a> | <a href="op_editFlagCriteria.jsp">Edit Flag Criteria</a></div>
</body>
</html>
