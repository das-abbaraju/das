<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title>Documents for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript">
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
	
	h3 {
		margin-top: 1em;
	}
</style>
</head>
<body>

<s:include value="conHeader.jsp" />

<s:if test="manuallyAddAudit">
	<a class="add" href="AuditOverride.action?id=<s:property value="id"/>">Create New Audit</a>
</s:if>

<s:iterator value="auditTypes.keySet()" id="classType" status="stat">
	<s:if test="#classType == 'IM' && imScores.keySet().size > 0">
		<h3><a name="<s:property value="#classType" />">Overall Integrity Management</a></h3>
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
					<td class="center"><s:property value="imScores.get(#key)"/></td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	
	<h3>
		<a name="<s:property value="#classType" />">
			<s:if test="#classType == 'IM'">Integrity Management Audits</s:if>
			<s:elseif test="#classType == 'AU'">Annual Updates</s:elseif>
			<s:else><s:property value="#classType" /></s:else>
		</a>
	</h3>
	<table class="report" id="table_<s:property value="#classType" />">
		<thead>
			<tr>
				<th>Name</th>
				<th>Safety Professional</th>
				<th>Scheduled</th>
				<th>View</th>
				<pics:permission perm="AuditCopy">
					<th>Copy</th>
				</pics:permission>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="auditTypes.get(#classType)" id="auditType">
				<s:iterator value="auditMap.get(#auditType)" id="audit">
					<tr>
						<td>
							<a href="Audit.action?auditID=<s:property value="#audit.id" />">
								<s:property value="#auditType.auditName" />
								<s:if test="#audit.auditFor.length() > 0">: <s:property value="#audit.auditFor" /></s:if>
								<s:if test="#classType == 'Policy'"><br /><span style="font-size: 10px"><s:date name="#audit.effectiveDate" format="MMM yyyy" /></span></s:if>
							</a>
						</td>
						<td><s:property value="#audit.auditor.name" /></td>
						<td><s:date name="#audit.scheduledDate" format="M/d/yy" /></td>
						<td>
							<s:if test="#audit.operators.size > 0">
								<a href="#" onclick="$('tr.row_'+<s:property value="#audit.id" />).toggle(); return false;">
									<s:iterator value="#audit.getCaoStats(permissions).keySet()" id="status" status="stat">
										<s:if test="getCaoStats(permissions).get(#status) > 1 || #audit.getCaoStats(permissions).keySet().size > 1"><s:property value="getCaoStats(permissions).get(#status)"/></s:if>
										<s:property value="#status"/><s:if test="!#stat.last">,</s:if>
									</s:iterator>
								</a>
							</s:if>
						</td>
						<pics:permission perm="AuditCopy">
							<td><a href="ConAuditCopy.action?auditID=<s:property value="#audit.id" />">Copy</a></td>
						</pics:permission>
					</tr>
					<s:if test="#audit.operators.size > 0">
						<tr class="row_<s:property value="#audit.id" /> hidden">
							<td colspan="4">
								<table class="inner">
									<s:iterator value="#audit.operators" id="cao">
										<s:if test="#cao.isVisibleTo(permissions)">
											<tr>
												<td>
													<pics:permission perm="ManageOperators">
														<a href="FacilitiesEdit.action?id=<s:property value="#cao.operator.id"/>"><s:property value="#cao.operator.name"/></a>
													</pics:permission>
													<pics:permission perm="ManageOperators" negativeCheck="true">
														<s:property value="#cao.operator.name" />
													</pics:permission>
												</td>
												<td><s:property value="#cao.status" /></td>
												<td><s:date name="#cao.statusChangedDate" format="M/d/yy" /></td>
												<s:if test="#classType == 'IM'">
													<td class="center"><s:property value="#audit.printableScore" /></td>
												</s:if>
											</tr>
										</s:if>
									</s:iterator>
								</table>
							</td>
						</tr>
					</s:if>
				</s:iterator>
			</s:iterator>
			<tr>
				<s:set name="colspan" value="4" />
				<pics:permission perm="AuditCopy">
					<s:set name="colspan" value="#colspan + 1" />
				</pics:permission>
				<td colspan="<s:property value="#colspan" />" class="center">
					<a href="#" onclick="showAll('table_<s:property value="#classType" />'); return false;" class="preview">View All</a>
				</td>
			</tr>
		</tbody>
	</table>
	
	<s:if test="expiredAudits.get(#classType).size > 0">
		<h3>Expired <s:property value="#classType == 'Policy' ? 'Policies' : #classType + 's'" /></h3>
		<table class="report" id="expired_<s:property value="#classType" />">
			<thead>
			<tr>
				<th>Name</th>
				<th>Safety Professional</th>
				<th>Expired</th>
				<th>View</th>
			</tr>
			</thead>
			<s:iterator value="expiredAudits.get(#classType)">
				<tr>
					<td>
						<a href="Audit.action?auditID=<s:property value="id" />">
							<s:property value="auditType.auditName" />
							<s:if test="auditFor.length() > 0">: <s:property value="#audit.auditFor" /></s:if>
							<s:if test="auditType.classType == 'Policy'"><br /><span style="font-size: 10px"><s:date name="effectiveDate" format="MMM yyyy" /></span></s:if>
						</a>
					</td>
					<td><s:property value="auditor.name" /></td>
					<td><s:date name="expiresDate" format="M/d/yyyy" /></td>
					<td>
						<s:if test="operators.size > 0">
							<a href="#" onclick="$('tr.row_'+<s:property value="id" />).toggle(); return false;">
								<s:iterator value="getCaoStats(permissions).keySet()" id="status" status="stat">
									<s:property value="getCaoStats(permissions).get(#status)" /> Operator<s:if test="getCaoStats(permissions).get(#status) > 1">s</s:if><s:if test="!#stat.last">,</s:if>
								</s:iterator>
							</a>
						</s:if>
					</td>
				</tr>
				<s:if test="operators.size > 0">
					<tr class="row_<s:property value="id" /> hidden">
						<td colspan="4">
							<table class="inner">
								<s:iterator value="operators" id="cao">
									<s:if test="#cao.isVisibleTo(permissions)">
										<tr>
											<td>
												<pics:permission perm="ManageOperators">
													<a href="FacilitiesEdit.action?id=<s:property value="#cao.operator.id"/>"><s:property value="#cao.operator.name"/></a>
												</pics:permission>
												<pics:permission perm="ManageOperators" negativeCheck="true">
													<s:property value="#cao.operator.name" />
												</pics:permission>
											</td>
											<td><s:property value="#cao.status" /></td>
											<td><s:date name="#cao.statusChangedDate" format="M/d/yy" /></td>
										</tr>
									</s:if>
								</s:iterator>
							</table>
						</td>
					</tr>
				</s:if>
			</s:iterator>
			<tr>
				<td colspan="<s:property value="#colspan" />" class="center">
					<a href="#" onclick="showAll('expired_<s:property value="#classType" />'); return false;" class="preview">View All</a>
				</td>
			</tr>
		</table>
	</s:if>
	
	<s:if test="#stat.last"><br clear="all" /></s:if>
</s:iterator>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
