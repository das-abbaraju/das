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
<script type="text/javascript" src="js/jquery/bbq/jquery.ba-bbq.min.js"></script>
<script type="text/javascript" src="js/jquery/facebox.js"></script>
<script type="text/javascript" src="js/con_audit.js?v=<s:property value="version"/>"></script>
</head>
<body>

<s:include value="../audits/audit_catHeader.jsp"/>
<table style="width: 100%">
	<tr style="width: 250px;">
		<td id="auditHeaderSideNav" class="auditHeaderSideNav noprint" style="width: 250px;">
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
					<s:iterator value="categories.keySet()" id="key">
						<s:iterator value="categories.get(#key)" id="cat" status="rowStatus">
							<s:if test="#cat.applies">
								<li id="category_<s:property value="categoryID"/>">
									<a class="hist-category" href="#categoryID=<s:property value="#cat.category.id" />"><s:property value="#cat.category.name" />
									<s:if test="conAudit.auditType.pqf">
										<span class="cat-percent">
											<s:if test="#cat.percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
											<s:else><s:property value="#cat.percentCompleted" />%</s:else>
										</span>
									</s:if>
									<s:if test="conAudit.auditType.hasRequirements">
										<span class="cat-percent">
											<s:if test="#cat.percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if>
											<s:else><s:property value="#cat.percentCompleted" />%</s:else>
										</span>
									</s:if>
									<s:if test="conAudit.auditType.id == 17">
										<span class="cat-percent">
											<s:property value="printableScore"/>
										</span>
									</s:if>
									</a>
								</li>
							</s:if>
						</s:iterator>
					</s:iterator>
				</ul>
				<ul id="nacatlist" class="catlist vert-toolbar">
					<li class="head">N/A CATEGORIES <span class="hidden-button">Show Others</span></li>
					<s:iterator value="categories.keySet()" status="rowStatus" id="cat">
						<s:if test="!applies && permissions.picsEmployee">
							<li id="category_<s:property value="categoryID"/>">
								<a class="hist-category" href="#categoryID=<s:property value="#cat.id" />"><s:property value="#cat.name" /></a>
							</li>
						</s:if>
					</s:iterator>
				</ul>
			</s:if>
		</td>
		<td id="auditViewArea">
		
		</td>
	</tr>
</table>

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
