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
<link rel="stylesheet" type="text/css" media="screen" href="css/pics.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<script type="text/javascript">
	function showFileUpload() {
		$('show_files').show();
	}
</script>
</head>
<body>

<div id="main">
<div id="bodyholder">
<div id="content">
<h1>Upload Files <span class="sub"><s:property value="conAudit.auditType.auditName" /> for <s:property value="contractor.name" /></span></h1>

<table class="report">
	<thead>
		<tr>
			<td>Description</td>
			<td>Uploaded</td>
			<td>View</td>
			<td>Edit</td>
			<s:if test="permissions.admin">
				<td>Reviewed</td>
			</s:if>
		</tr>
	</thead>
	<s:iterator value="auditFiles">
		<tr>
			<td><s:property value="description" /></td>
			<td><nobr><s:date name="creationDate"
				format="MM/dd hh:mm" /></nobr></td>
			<td><a
				href="ContractorAuditFileUpload.action?auditID=<s:property value="conAudit.id"/>&fileID=<s:property value="id"/>&button=download"
				target="_BLANK"><img src="images/icon_DA.gif" /></a></td>
			<td><s:if
				test="permissions.admin || (!reviewed && permissions.userId == createdBy.Id)">
				<a class="edit" href="#" onclick="showFileUpload();">Edit</a>
			</s:if></td>
			<s:if test="permissions.admin">
				<td class="center"><s:if test="reviewed">
					<span class="verified" style="font-size: 16px;"></span>
				</s:if> <s:else>
					<a class="verify"
						href="ContractorAuditFileUpload.action?auditID=<s:property value="conAudit.id"/>&fileID=<s:property value="id"/>&button=Review">Mark
					As Reviewed</a>
				</s:else></td>
			</s:if>
		</tr>
	</s:iterator>
</table>
<br clear="all" />
<s:if test="!permissions.operatorCorporate">
	<div><input type="button" class="picsbutton positive"
		value="Add File" onclick="showFileUpload('show_files')" /></div>
</s:if> <br clear="all" />
<s:include value="../actionMessages.jsp" /> <br clear="all" />
<div id="show_files" style="display: none;"><s:include
	value="con_audit_file_upload.jsp" /></div>
</div>
</div>
</div>
</body>
</html>