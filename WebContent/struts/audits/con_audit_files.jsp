<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="css/audit.css" />
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
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


<h1>Upload Open Requirements <span class="sub"><s:property value="conAudit.auditType.auditName" /> for <s:property value="contractor.name" /></span></h1>
<s:include value="../actionMessages.jsp" />

Click on the Open Requirement(s) below to upload supporting documentation. <br/><b>Please allow up to 7 days for an auditor to review your documentation.</b> 

<br clear="all"/>
<br clear="all"/>

<table class="report">
	<tr>
		<td>
			<table class="report">
				<thead>
					<tr>
						<td>Open Requirement</td>
					</tr>
				</thead>
				<s:iterator value="openReqs" id="data">
					<tr>
						<s:set name="fileDesc" value="getFileDesc(#data.question)"/>
						<td onclick="javascript: showAuditUpload(<s:property value="conAudit.id"/>,0,'<s:property value="#fileDesc"/>',<s:property value="question.id"/>); return false;">
							<s:property value="#fileDesc"/>&nbsp;&nbsp; 
						<s:property value="question.requirement" /><br/>
						<s:if test="!comment.toString().equals('null')">
							<span class="redMain"><b>Auditor Comment : </b><s:property value="comment"/></span>
						</s:if>
						</td>
					</tr>
				</s:iterator>
			</table>		
		</td>
		<td style="vertical-align:top;">
			<table class="report">
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