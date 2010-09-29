<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>
</head>
<body>

<s:include value="../audits/audit_catHeader.jsp"/>
<div class="right" id="modes">
	<s:if test="canEditAudit">
		<a class="edit modeset" href="#mode=Edit">Edit</a>
	</s:if>
	<a class="view modeset" href="#mode=View">View</a>
	<s:if test="canVerifyAudit">
		<a class="verify modeset" href="#mode=Verify">Verify</a> 
	</s:if>
</div>

<table id="audit-layout">
	<tr>
		<td id="auditHeaderSideNav" class="auditHeaderSideNav noprint">
			<ul id="toolbar" class="vert-toolbar"> 
				<li class="head">TOOLBAR</li>
				<pics:permission perm="AuditEdit">
					<li><a class="edit" href="ConAuditMaintain.action?auditID=<s:property value="auditID" />"
							<s:if test="requestURI.contains('audit_maintain.jsp')">class="current"</s:if>>System Edit</a></li>
				</pics:permission>
				<s:if test="canVerify">
					<pics:permission perm="AuditVerification">
						<li><a class="verify" href="VerifyView.action?id=<s:property value="id" />"
						<s:if test="requestURI.contains('verif')">class="current"</s:if>>Verify</a></li>
					</pics:permission>
				</s:if>
				<s:if test="canPreview">
					<li><a class="preview" href="Audit.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
					Questions</a></li>
				</s:if>
				<s:if test="canViewRequirements">
					<li><a class="print" href="Audit.action?auditID=<s:property value="auditID"/>&onlyReq=true" 
						<s:if test="onlyReq && mode != 'Edit'">class="current"</s:if>>Print Requirements</a></li>
					<s:if test="permissions.auditor">
						<li><a class="edit" href="Audit.action?auditID=<s:property value="auditID"/>&onlyReq=true&mode=Edit"
						 <s:if test="onlyReq && mode == 'Edit'">class="current"</s:if>>Edit Requirements</a></li>
					</s:if>
					<s:if test="permissions.admin">
						<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
					</s:if>
					<s:elseif test="permissions.onlyAuditor">
						<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
					</s:elseif>
					<s:if test="permissions.operatorCorporate">
						<li><a class="file" href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Review Requirements</a></li>
					</s:if>
				</s:if>
				<s:if test="!singlePageAudit">
					<pics:permission perm="AllContractors">
						<li><a class="refresh" href="?auditID=<s:property value="auditID"/>&button=recalculate">Recalculate Categories</a></li>
					</pics:permission>
				</s:if>
				<s:if test="canSchedule">
					<li><a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>"
							<s:if test="requestURI.contains('schedule_audit')">class="current"</s:if>>Schedule Audit</a></li>
				</s:if>
			</ul>
			<s:if test="categories.keySet().size > 1">
				<ul id="catlist" class="catlist vert-toolbar">
					<li class="head">CATEGORIES <span class="hidden-button">Show N/A</span></li>			
					<s:iterator value="categories">
						<s:if test="key.parent == NULL && value.applies">
							<li id="category_<s:property value="key.id"/>">
								<a class="hist-category" href="#categoryID=<s:property value="key.id" />"><s:property value="key.name" />
								<s:if test="conAudit.auditType.pqf">
									<span class="cat-percent">
										<s:if test="value.percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
										<s:else><s:property value="value.percentCompleted" />%</s:else>
									</span>
								</s:if>
								<s:elseif test="conAudit.auditType.workflow.hasSubmittedStep">
									<span class="cat-percent">
										<s:if test="value.percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
										<s:else><s:property value="value.percentVerified" />%</s:else>
									</span>
								</s:elseif>
								<s:elseif test="conAudit.auditType.id == 17">
									<span class="cat-percent">
										<s:if test="value.printableScore == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
										<s:else><s:property value="value.printableScore" />%</s:else>
									</span>
								</s:elseif>
								</a>
							</li>
						</s:if>
					</s:iterator>
				</ul>
				<ul id="nacatlist" class="catlist vert-toolbar">
					<li class="head">N/A CATEGORIES <span class="hidden-button">Back</span></li>
					<s:iterator value="categories" status="rowStatus">
						<s:if test="key.parent == NULL && !value.applies && permissions.picsEmployee">
							<li id="category_<s:property value="key.id"/>">
								<a class="hist-category" href="#categoryID=<s:property value="key.id" />"><s:property value="key.name" /></a>
							</li>
						</s:if>
					</s:iterator>
				</ul>
			</s:if>
		</td>
		<td>
			<div id="auditViewArea"></div>
		</td>
	</tr>
</table>

<s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
	<div class="info">
		<b>Safety Professional Notes:</b> <s:property value="auditorNotes"/>
	</div>
</s:if>
<br clear="all"/>
</body>
</html>
