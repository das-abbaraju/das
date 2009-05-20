<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>InsureGuard&trade; for <s:property value="contractor.name" /></title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<script type="text/javascript" src="js/prototype.js"></script>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css" />
<script src="js/notes.js" type="text/javascript"></script>
<script type="text/javascript">
	function showAddAudit() {
	$('addAudit').hide();	
	$('addAuditManually').show();
	}

	function showCertUpload(conid, certid) {
		url = 'CertificateUpload.action?id='+conid+'&certID='+certid;
		title = 'Upload';
		pars = 'scrollbars=yes,resizable=yes,width=650,height=450,toolbar=0,directories=0,menubar=0';
		fileUpload = window.open(url,title,pars);
		fileUpload.focus();
	}
</script>
</head>
<body>
<s:push value="#subHeading='InsureGuard&trade;'"/>
<s:include value="conHeader.jsp" />
<table>
<tr><td>
<h3> Requested Insurance Policies (NEW) </h3>
<table class="report">
	<thead>
		<tr>
			<th>Policy Type</th>
			<th>Operator</th>
			<th>Status</th>
			<th>File</th>
		</tr>
	</thead>
	<s:iterator value="requested">
	<tr>
		<td rowspan="<s:property value="value.size+1"/>"><s:property value="key.auditType.auditName"/></td>
	</tr>
		<s:iterator value="value">
		<tr>
			<td><s:property value="operator.name"/></td>
			<td class="Amber"><s:property value="status"/></td>
			<td></td>
		</tr>
		</s:iterator>
	</s:iterator>
</table>

<br/>

<h3> Current Insurance Policies (NEW) </h3>
<table class="report">
	<thead>
		<tr>
			<th>Policy Type</th>
			<th>Operator</th>
			<th>Status</th>
			<th>File</th>
		</tr>
	</thead>
	<s:iterator value="current">
	<tr>
		<td rowspan="<s:property value="value.size+1"/>"><s:property value="key.auditType.auditName"/></td>
	</tr>
		<s:iterator value="value">
		<tr>
			<td><s:property value="operator.name"/></td>
			<td class="<s:if test="status.approved">Green</s:if><s:else>Red</s:else>"><s:property value="status"/></td>
			<td></td>
		</tr>
		</s:iterator>
	</s:iterator>
</table>

<!--<s:if test="requiredAuditTypeNames.size > 0 || upComingAudits.size > 0">
>>>>>>> .r5432
	<h3>Requested Insurance Policies</h3>
	<table class="report">
		<thead>
		<tr>
			<th>Op Status</th>
			<th>Type</th>
			<th>Operator</th>
			<s:if test="certificatesFiles.size() > 0">
				<th>File</th>
			</s:if>
		</tr>
		</thead>
		<s:iterator id="conAudit" value="upComingAudits" status="auditStatus">
			<tr>
				<td>
					<s:iterator value="currentOperators">
						<s:if test="permissions.operator || permissions.corporate">
							<s:if test="operator.id == permissions.accountId || operator.isDescendantOf(permissions.accountId) || permissions.corporateParent.contains(operator.id)">
								<s:if test="status.pending||status.submitted||status.verified">
									<s:property value="operator.name" /> - <span class="Amber"><s:property
										value="status" /></span>
									<br />
								</s:if>
							</s:if>
						</s:if>
						<s:else>
							<s:if test="status.pending||status.submitted||status.verified">
								<s:property value="operator.name" /> - <span class="Amber"><s:property
									value="status" /></span>
								<br />
							</s:if>
						</s:else>
					</s:iterator>
				</td>
				<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
				<s:if test="requestingOpAccount.name != null">
					<td><s:property value="requestingOpAccount.name" /></td>
				</s:if>
				<s:else>
					<td>Shared</td>
				</s:else>
				<s:if test="certificatesFiles.size() > 0">
					<td><s:iterator value="certificatesFiles">
							<s:if test="audit.id == #conAudit.id">
								<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" 
								target="_BLANK" 
								title="<s:if test="parentAnswer.answer != null"><s:property value="parentAnswer.answer"/></s:if>"><img src="images/icon_insurance.gif"/>
								</a>
							</s:if>
						</s:iterator>
					</td>
				</s:if>
			</tr>
		</s:iterator>
		<s:if test="manuallyAddAudit">
			<s:if test="auditTypeName.size > 0">
				<tr>
					<td id="addAudit" colspan="4" class="center">
						<a href="#" onclick="showAddAudit(); return false;">Add New Policy</a>
					</td>
				</tr>
			</s:if>
			<tr id="addAuditManually" style="display: none;">
			<s:form method="post" id="form1" >
				<td>
				<div class="buttons">
					<input class="picsbutton positive" name="button" type="submit" value="Add"/>
				</div>
				</td>
				<td>
					<s:hidden name="id" value="%{id}"/>
					<s:if test="permissions.admin">
						<s:select list="auditTypeName" name="selectedAudit" cssClass="pics"
							headerKey="" headerValue="- Select Policy Type -" listKey="id" listValue="auditName" />
					</s:if>
					<s:elseif test="permissions.contractor">
						<s:select list="requiredAuditTypeNames" name="selectedAudit" cssClass="pics"
							headerKey="" headerValue="- Select Policy Type -" listKey="id" listValue="auditName" />
					</s:elseif>
				</td>
				<td>
					<s:if test="permissions.contractor || permissions.admin">
						<s:select list="operatorsWithInsurance" name="selectedOperator"
							headerKey="" headerValue="- Shared by All Operators -" listKey="operatorAccount.id" listValue="operatorAccount.name" />
					</s:if>
				</td>
				<s:if test="certificatesFiles.size() > 0">
					<td></td>
				</s:if>
				</s:form>
			</tr>
		</s:if>
	</table>
</s:if>


<s:if test="currentAudits.size() > 0">
<br/>
<h3>Current Policies</h3>
<table class="report">
	<thead>
	<tr>
		<th>Op Status</th>
		<th>Type</th>
		<th>Effective</th>
		<th>Expires</th>
		<s:if test="certificatesFiles.size() > 0">
			<th>File</th>
		</s:if>
	</tr>
	</thead>
	<s:iterator id="conAudit" value="currentAudits" status="auditStatus">
		<tr>
		<td>
			<s:iterator value="currentOperators">
				<s:if test="permissions.operator || permissions.corporate">
					<s:if test="operator.id == permissions.accountId || operator.isDescendantOf(permissions.accountId) || permissions.corporateParent.contains(operator.id)">
						<s:if test="status.approved">
							<s:property value="operator.name" /> - <span class="Green"><s:property
								value="status" /></span>
							<br />
						</s:if>
						<s:elseif test="status.rejected">
							<s:property value="operator.name" /> - <span class="Red"><s:property
								value="status" /></span>
							<br />
						</s:elseif>
					</s:if>
				</s:if>
				<s:else>
					<s:if test="status.approved">
						<s:property value="operator.name" /> - <span class="Green"><s:property
							value="status" /></span>
						<br />
					</s:if>
					<s:elseif test="status.rejected">
						<s:property value="operator.name" /> - <span class="Red"><s:property
							value="status" /></span>
						<br />
					</s:elseif>
				</s:else>
			</s:iterator>
		</td>
		<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
		<td><s:date name="creationDate" format="M/d/yy" /></td>
		<td><s:date name="expiresDate" format="M/d/yy" /></td>
		<s:if test="certificatesFiles.size() > 0">
			<td><s:iterator value="certificatesFiles">
				<s:if test="audit.id == #conAudit.id">
					<a href="DownloadAuditData.action?auditID=<s:property value="audit.id"/>&answer.id=<s:property value="id"/>" 
					target="_BLANK" 
					title="<s:if test="parentAnswer.answer != null"><s:property value="parentAnswer.answer"/></s:if>"><img src="images/icon_insurance.gif"/>
					</a>
				</s:if>
				</s:iterator>
			</td>
		</s:if>
		</tr>
	</s:iterator>
</table>
</s:if>
-->

<s:if test="expiredAudits.size() > 0">
<br/>
<h3>Expired Policies</h3>
<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>Effective</th>
		<th>Expired</th>
	</tr>
	</thead>
	<s:iterator value="expiredAudits" status="auditStatus">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:date name="creationDate" format="M/d/yy" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
</s:if>
</td>
<td>
	<h3>Certificates</h3>
	<table class="report">
		<thead>
			<tr><th>FileName</th>
			<th>CreatedDate</th>
			<th></th></tr>
		</thead>
		<s:iterator value="certificates">
			<tr>
				<td>
					<a href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download" 
					target="_BLANK"><s:property value="description"/></a>
				</td>
				<td><s:date name="creationDate" format="M/d/yy"/></td>
				<td><a class="edit"
					href="#" onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />)" title="Opens in new window (please disable your popup blocker)"">Edit</a></td>
			</tr>
		</s:iterator>
	</table>
	<div class="buttons">
		<input type="button" class="picsbutton positive" value="Add File" onclick="showCertUpload(<s:property value="id" />, 0)" title="Opens in new window (please disable your popup blocker)"/>
	</div>
</td>
</tr>
</table>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
