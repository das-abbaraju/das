<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="js/jquery/blockui/blockui.css?v=<s:property value="version"/>" />
<s:include value="../jquery.jsp"/>
<script type="text/javascript" src="js/jquery/blockui/jquery.blockui.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css?v=<s:property value="version"/>" />
<script type="text/javascript">
function showAuditUpload(auditID, fileID, desc, question) {
	url = 'AuditFileUpload.action?auditID='+auditID+'&fileID='+fileID+'&desc='+desc+'&question='+question;
	title = 'Upload';
	pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
	fileUpload = window.open(url,title,pars);
	fileUpload.focus();
}
</script>
</head>
<body>

<s:include value="../audits/audit_catHeader.jsp"/>

<s:include value="../actionMessages.jsp" />
<div class="info">
<b>Click on the Open Requirement(s) below to upload supporting documentation. If you have any further questions or believe that a section does not apply to your company, please contact the safety professional below.
<br/>*** Please allow up to 3 days for a safety professional to review your files. ***</b>   
</div>
<ul>
<h3><s:property value="conAudit.auditType.auditName" />
#<s:property value="conAudit.id" /></h3>

<li style="list-style-type: none;"><b>Safety Professional: </b> 
	<s:if test="conAudit.closingAuditor != null"><s:property value="conAudit.closingAuditor.name"/></s:if>
	<s:else><s:property value="conAudit.auditor.name"/></s:else>
</li>
<li style="list-style-type: none;"><b>Phone: </b> 
	<s:if test="conAudit.closingAuditor != null"><s:property value="conAudit.closingAuditor.phone"/></s:if>
	<s:else><s:property value="conAudit.auditor.phone"/></s:else>
</li>
<li style="list-style-type: none;"><b>Email: </b> 
	<s:if test="conAudit.closingAuditor != null"><a href="mailto:<s:property value="conAudit.closingAuditor.email"/>"><s:property value="conAudit.closingAuditor.email"/></a></s:if>
	<s:else><a href="mailto:<s:property value="conAudit.auditor.email"/>"><s:property value="conAudit.auditor.email"/></a></s:else>
</li>
</ul>
<table style="background-color:none; border:none; margin:10px;">
	<tr>
		<td style="vertical-align:top; width:65%;">
		<h3 style="padding:10px 0 10px 0;">Open Requirements</h3>
			<table class="report" style="width:95%;">
				<thead>
					<tr>
						<td>Requirements</td>
					</tr>
				</thead>
				<s:iterator value="openReqs" id="data">
					<tr>
						<td>
							<s:set name="fileDesc" value="getFileDesc(#data.question)"/>
							<s:property value="#fileDesc"/>&nbsp;&nbsp; 
							<s:if test="permissions.operatorCorporate">
								<s:property value="question.requirement" />
							</s:if>
							<s:else>
								<a style="cursor:pointer;" onclick="javascript: showAuditUpload(<s:property value="conAudit.id"/>,0,'<s:property value="#fileDesc"/>',<s:property value="question.id"/>); return false;" title="Click here to upload requirements"><s:property value="question.requirement" /></a>
							</s:else>	
							<br/>
							<s:if test="comment.length() > 0">
								<span class="redMain"><b>Safety Professional Comment : </b><s:property value="comment" escape="false"/></span>
							</s:if>
							<s:if test="permissions.auditor">
							<br/>
								<a href="Audit.action?auditID=<s:property value="#data.audit.id"/>#categoryID=<s:property value="#data.question.category.id"/>&onlyReqs=true&mode=Edit">Close Requirement</a>									
							</s:if>
						</td>
					</tr>
				</s:iterator>
			</table>		
		</td>
		<td style="vertical-align:top;">
		<h3 style="padding:10px 0 10px 0;">Documents</h3>
			<table class="report" style="width:95%;">
				<thead>
					<tr>
						<td>Description</td>
						<td>View</td>
						<td>Edit</td>
						<td>Reviewed</td>
					</tr>
				</thead>
				<s:iterator value="auditFiles">
					<tr>
						<td><s:property value="description" /></td>
						<td><a
							href="AuditFileUpload.action?auditID=<s:property value="conAudit.id"/>&fileID=<s:property value="id"/>&button=download"
							target="_BLANK"><img src="images/icon_DA.gif" /></a></td>
						<td> 
						<s:if
							test="permissions.admin || (!reviewed && permissions.userId == createdBy.Id)">
							<a class="edit" href="#" onclick="showAuditUpload(<s:property value="conAudit.id"/>,<s:property value="id"/>, null, 0);">Edit</a>
						</s:if></td>
							<td class="center">
							<s:if test="reviewed">
								<span class="verified" style="font-size: 16px;"></span>
							</s:if> 
							<s:else>
								<s:if test="permissions.auditor">
								<a class="verify"
									href="ContractorAuditFileUpload.action?auditID=<s:property value="conAudit.id"/>&fileID=<s:property value="id"/>&button=Review">Mark
								As Reviewed</a>
								</s:if>
							</s:else>
							</td>
					</tr>
				</s:iterator>
			</table>
		</td>
	</tr>
</table>
<br clear="all">

</body>
</html>