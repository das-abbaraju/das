<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>Documents for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
	function showAddAudit() {
		$('#addAudit').hide();	
		$('#addAuditManually').show();
	}

	function getAuditList(classType) {
		var data = {
			button: 'getAuditList',
			auditClass: classType,
			id: <s:property value="id" />
		}

		$('#auditListLoad').load('ContractorDocumentsAjax.action', data);
	}

	function showAll(type) {
		if ($('#' + type + ' tr.hidden:hidden').length > 0)
			$('#'+type + ' tr.hidden').show();
		else
			$('#'+type + ' tr.hidden').hide();
	}
</script>
<style type="text/css">
	.hidden {
		display: none;
		font-size: smaller;
		background-color: #f3f3f3;
	}
	
	.header td {
		color: #fff;
		background-color: #013264;
	}
</style>
</head>
<body>
<s:push value="#subHeading='Contractor Forms, Audits & Evaluations'"/>
<s:include value="conHeader.jsp" />

<s:if test="manuallyAddAudit">
	<a href="#" onclick="showAddAudit(); return false;" id="addAudit" class="add">Add Audit Manually</a>
	<div id="addAuditManually" style="display: none;">
		<s:form method="post" id="form1">
			<fieldset class="form">
				<h2 class="formLegend">Add Audit Manually</h2>
				<ol>
					<li><label>Audit Class</label>
						<s:select list="#{'PQF':'PQF','Audit':'Audit','IM':'Integrity Management'}" name="auditClass" onchange="getAuditList(this.value);" />
					</li>
					<li id="auditListLoad"><script type="text/javascript">getAuditList('PQF');</script></li>
					<li><label>For</label>
						<s:textfield name="auditFor" maxlength="50" />
					</li>
					<pics:permission perm="AllOperators">
						<li><label>Operators</label>
							<s:select list="operators" listKey="operatorAccount.id" listValue="operatorAccount.name"
								name="selectedOperator" headerKey="" headerValue="- Shared by All Operators -"/>
						</li>
					</pics:permission>
				</ol>
			</fieldset>
			<fieldset class="form submit">
				<input type="submit" class="picsbutton positive" name="button" type="submit" value="Add" />
			</fieldset>
		</s:form>
	</div>
	<br />
</s:if>

<s:iterator value="auditTypes.keySet()" id="classType">
	<s:if test="#classType.toString() == 'IM' && imScores.keySet().size > 0">
		<h3>Integrity Management Scores For This Contractor</h3>
		<table class="report">
			<thead>
			<tr>
				<th>Audit Name</th>
				<th>Overall Score</th>
			</tr>
			</thead>
			<s:iterator value="imScores.keySet()" status="auditStatus" id="key">
				<tr>
					<td><s:property value="#key"/></td>
					<td><s:property value="imScores.get(#key)"/></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>

	<h3>
		<s:if test="#classType.toString() != 'IM'">
			<s:property value="#classType.toString()" />
		</s:if>
		<s:else>
			Integrity Management
		</s:else>
	</h3>
	<table class="report" id="<s:property value="#classType" />">
		<thead>
			<tr>
				<th>Type</th>
				<th>For</th>
				<th colspan="2">Safety Professional</th>
				<th colspan="2">Scheduled</th>
				<th>View</th>
				<s:if test="#classType.toString() == 'IM'">
					<th>Score</th>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="auditTypes.get(#classType)" id="auditType">
				<s:iterator value="auditMap.get(#auditType)" id="audit">
					<tr>
						<td>
							<a href="Audit.action?auditID=<s:property value="#audit.id" />"><s:property value="#auditType.auditName" /></a>
						</td>
						<td><s:property value="#audit.auditFor" /></td>
						<td colspan="2"><s:property value="#audit.auditor.name" /></td>
						<td colspan="2"><s:date name="#audit.scheduledDate" format="M/d/yy" /></td>
						<td>
							<a href="#" onclick="$('tr.row_'+<s:property value="#audit.id" />).toggle(); return false;">
								<s:iterator value="counts.get(#audit).keySet()" id="status" status="stat">
									<s:property value="counts.get(#audit).get(#status).size()" /> <s:property value="#status.toString()" /><s:if test="!#stat.last">,</s:if>
								</s:iterator>
							</a>
						</td>
						<s:if test="#classType.toString() == 'IM'">
							<td><s:property value="imScores.get(#audit.auditName)" /></td>
						</s:if>
					</tr>
					<tr class="row_<s:property value="#audit.id" /> hidden header">
						<td class="center">Operator</td>
						<td class="center">Updated</td>
						<td class="center">% Complete</td>
						<td class="center">Created</td>
						<td class="center">Submitted</td>
						<td class="center">Completed</td>
						<td class="center">Status</td>
						<s:if test="#classType.toString() == 'IM'">
							<td class="center">Score</td>
						</s:if>
					</tr>
					<s:iterator value="#audit.operators" id="cao">
						<tr class="row_<s:property value="#audit.id" /> hidden">
							<td>
								<pics:permission perm="ManageOperators">
									<a href="FacilitiesEdit.action?id=<s:property value="#cao.operator.id"/>"><s:property value="#cao.operator.name"/></a>
								</pics:permission>
								<pics:permission perm="ManageOperators" negativeCheck="true">
									<s:property value="#cao.operator.name" />
								</pics:permission>
							</td>
							<td><s:date name="#cao.statusChangedDate" format="M/d/yy" /></td>
							<td class="right"><s:property value="#cao.percentComplete" /></td>
							<td><s:date name="#cao.creationDate" format="M/d/yy" /></td>
							<td><s:date name="#cao.submittedDate" format="M/d/yy" /></td>
							<td><s:date name="#cao.completedDate" format="M/d/yy" /></td>
							<td><s:property value="#cao.status" /></td>
							<s:if test="#classType.toString() == 'IM'">
								<td class="center"><s:property value="#audit.printableScore" /></td>
							</s:if>
						</tr>
					</s:iterator>
				</s:iterator>
			</s:iterator>
		</tbody>
	</table>
	<a href="#" onclick="showAll('<s:property value="#classType.toString()" />'); return false;" class="preview">View All</a>
</s:iterator>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
