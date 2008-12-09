<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="conAudit.auditType.auditName" /> for
<s:property value="conAudit.contractorAccount.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<script src="js/prototype.js" type="text/javascript"></script>
<script type="text/javascript" src="js/scriptaculous/scriptaculous.js?load=effects"></script>
<script type="text/javascript">
	function resubmitPqf() {
		var data = $('confirm').checked;
		if(!data) {
			alert("Please check the check box");
			return;
		}
		var pars = 'auditID='+<s:property value="auditID"/>+'&button=ReSubmit';
		var myAjax = new Ajax.Updater('','ResubmitAuditAjax.action', 
		{
			method: 'post', 
			parameters: pars,
		});
		return false;
	}
</script>
</head>
<body>
<s:include value="conHeader.jsp" />

<s:form>
	<s:hidden name="auditID" />
	<s:if test="canSubmit">
		<s:hidden name="auditStatus" value="Submitted" />
	</s:if>
	<s:if test="conAudit.auditStatus.toString() == 'Pending'">
		<s:submit value="%{'Submit '.concat(conAudit.auditType.auditName)}" disabled="!canSubmit" />
		<span class="redMain">&nbsp;&nbsp;&nbsp;All sections must be filled out before submitting.</span>
	</s:if>
	<s:if test="canClose">
		<s:hidden name="auditStatus" value="Active" />
	</s:if>
	<s:if test="conAudit.auditStatus.toString() == 'Submitted'">
		<s:submit value="%{'Close '.concat(conAudit.auditType.auditName)}" disabled="!canClose" />
	</s:if>
</s:form>
<s:if test="canResubmit">
	<div id="info">
		<s:checkbox id="confirm" name="''"/> I verify that all information contained in this form is correct and up to date to the best of my ability.<br/>
		<s:submit value="Resubmit PQF" onclick="resubmitPqf()"></s:submit>
	</div>
</s:if>


<table class="report">
	<thead>
		<tr>
			<th>Num</th>
			<th>Category</th>
		<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
			<th colspan="2">Complete</th>
		</s:if>
		<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements && !conAudit.auditType.pqf">
			<th colspan="2">Requirements</th>
		</s:if>
		<s:if test="canApply">
			<th>Apply</th>
		</s:if>
		</tr>
	</thead>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="appliesB">
			<tr>
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></td>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.category" /></a></td>
			<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
				<td class="right"><s:property value="percentCompleted" />%</td>
				<td><s:if test="percentCompleted == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements && !conAudit.auditType.pqf">
				<td class="right"><s:property value="percentVerified" />%</td>
				<td><s:if test="percentVerified == 100"><img src="images/okCheck.gif" width="19" height="15" /></s:if></td>
			</s:if>
			<s:if test="canApply">
				<td><s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="removeCategoryID" value="%{id}" /><s:submit value="Remove"></s:submit></s:form></td>
			</s:if>
			</tr>
		</s:if>
	</s:iterator>
	<s:iterator value="categories" status="rowStatus">
		<s:if test="!appliesB && permissions.picsEmployee">
			<tr class="notapp">
				<td class="right"><a name="<s:property value="id" />"><s:property value="category.number" /></td>
				<td><a href="AuditCat.action?auditID=<s:property value="auditID" />&catDataID=<s:property value="id" />"><s:property value="category.category" /></a></td>
				<s:if test="conAudit.auditStatus.name() == 'Pending' || conAudit.auditType.pqf">
					<td class="center" colspan="2">N/A</td>
				</s:if>
				<s:if test="conAudit.auditStatus.name() == 'Submitted' && conAudit.auditType.hasRequirements && !conAudit.auditType.pqf">
					<td colspan="2"></td>
				</s:if>
				<s:if test="canApply">
					<td>
					<s:form method="POST"><s:hidden name="auditID" value="%{auditID}" /><s:hidden name="applyCategoryID" value="%{id}"></s:hidden><s:submit value="Add"></s:submit></s:form>
					</td>
				</s:if>
			</tr>
		</s:if>
	</s:iterator>
</table>

</body>
</html>
