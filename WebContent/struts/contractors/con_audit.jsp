<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script>
$(function(){
	$('#naCats').hide();	
	if($('.notApplicable').length>0)
		$('#showHideCats').show();
});
function showHideCats(){
	$('#appCats').toggle();
	$('#naCats').toggle();
	if($('#appCats').is(":hidden") )
		$('#showHideCats').text("Show Applicable Categories");
	else
		$('#showHideCats').text("Show Not Applicable Categories");
}
</script>
</head>
<body>

<s:include value="../audits/audit_catHeader.jsp"/>
<div id="auditContentArea" style="width: 100%">
	<div id="auditHeaderSideNav" class="auditHeaderSideNav noprint">
		<div id="toolbar">
			<ul>
			<li class="head">TOOLBAR</li>
			<pics:permission perm="AuditEdit">
						<li><a href="ConAuditMaintain.action?auditID=<s:property value="auditID" />"
							<s:if test="requestURI.contains('audit_maintain.jsp')">class="current"</s:if>>System Edit</a></li>
					</pics:permission>
					<s:if test="conAudit.auditStatus.pendingSubmittedResubmitted || conAudit.auditStatus.incomplete && (conAudit.auditType.pqf || conAudit.auditType.annualAddendum)">
						<pics:permission perm="AuditVerification">
							<li><a href="VerifyView.action?id=<s:property value="id" />"
							<s:if test="requestURI.contains('verif')">class="current"</s:if>>Verify</a></li>
						</pics:permission>
					</s:if>
					<s:if test="conAudit.auditStatus.pending">
						<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&mode=ViewQ">Preview
						Questions</a></li>
					</s:if>
					<s:if test="conAudit.auditType.hasRequirements && conAudit.auditStatus.activeSubmitted">
						<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true" 
							<s:if test="onlyReq && mode != 'Edit'">class="current"</s:if>>Print Requirements</a></li>
						<s:if test="permissions.auditor">
							<li><a href="AuditCat.action?auditID=<s:property value="auditID"/>&onlyReq=true&mode=Edit"
							 <s:if test="onlyReq && mode == 'Edit'">class="current"</s:if>>Edit Requirements</a></li>
						</s:if>
						<s:if test="permissions.admin">
							<li><a href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
						</s:if>
						<s:elseif test="permissions.onlyAuditor">
							<li><a href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Upload Requirements</a></li>
						</s:elseif>
						<s:if test="permissions.operatorCorporate">
							<li><a href="ContractorAuditFileUpload.action?auditID=<s:property value="auditID"/>">Review Requirements</a></li>
						</s:if>
					</s:if>
					<s:if test="!singleCat">
						<pics:permission perm="AllContractors">
							<li><a href="?auditID=<s:property value="auditID"/>&button=recalculate">Recalculate Categories</a></li>
						</pics:permission>
					</s:if>
					<s:if test="(permissions.contractor || permissions.admin) && conAudit.auditStatus.pending && conAudit.auditType.scheduled">
						<li><a href="ScheduleAudit.action?auditID=<s:property value="conAudit.id"/>"
								<s:if test="requestURI.contains('schedule_audit')">class="current"</s:if>>Schedule Audit</a></li>
					</s:if>
			</ul>
		</div>
		<div id="categories">
		<ul>
			<li class="head">CATEGORIES</li>			
			<li id="appCats" class="noStyle"> 
				<ul class="nestedUL">
					<s:iterator value="categories" status="rowStatus">
						<s:if test="applies">
							<li>
								<a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.name" />
								<s:if test="conAudit.auditType.pqf">
									<span style="float: right;">
										<s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
										<s:else><s:property value="percentCompleted" />%</s:else>
									</span>
									<div class="clear"></div>
								</s:if>
								<s:if test="conAudit.auditType.hasRequirements">
									<span style="float: right;">
										<s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
										<s:else><s:property value="percentCompleted" />%</s:else>
									</span>
									<div class="clear"></div>
								</s:if>
								</a>
								<% /*<s:if test="canApply">
									<td><s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="removeCategoryID" value="%{id}" /><s:submit value="Remove"></s:submit></s:form></td>
								</s:if>*/%>
								<s:if test="conAudit.auditType.id == 17">
									<s:property value="printableScore"/>
								</s:if>
							</li>
						</s:if>
					</s:iterator>
				</ul>
			</li>
			<li id="naCats" class="noStyle"> 
				<ul class="nestedUL">
				<s:iterator value="categories" status="rowStatus">
					<s:if test="!applies && permissions.picsEmployee">
						<li class="notApplicable">
						<a class="inactive" href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.name" /></a>
						<s:if test="canApply">
							<%/*<td>
							<s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="applyCategoryID" value="%{id}"></s:hidden><s:submit value="Add"></s:submit></s:form>
							</td>*/ %>
						</s:if>
						</li>
					</s:if>
				</s:iterator>
				</ul>
			</li>
		</ul>
		</div>
		<div id="showHideCats" onclick="showHideCats(); return false;">Show Not Applicable Categories</div>
	</div>
	<div class="auditViewArea">
		view area, load cats here
	</div>
	<div class="clear"></div>
</div>

<s:if test="!@com.picsauditing.util.Strings@isEmpty(auditorNotes)">
	<div class="info">
		<b>Safety Professional Notes:</b> <s:property value="auditorNotes"/>
	</div>
</s:if>
<s:if test="conAudit.auditType.pqf">
	<s:if test="permissions.operatorCorporate && conAudit.auditStatus.active && conAudit.percentComplete < 100">
		<div class="info">
 			This PQF was Completed and Active as of <s:date name="conAudit.completedDate" format="MMM d, yyyy" />. 
 			Some sections have been added since this date and will be addressed in January.
		</div>
	</s:if>
</s:if>

<br clear="all"/>
</body>
</html>
