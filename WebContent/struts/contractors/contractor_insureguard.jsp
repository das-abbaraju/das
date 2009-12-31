<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>InsureGUARD&trade; for <s:property value="contractor.name" /></title>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091231" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
<script type="text/javascript">
	function showAddAudit() {
		$('#addAudit').hide();	
		$('#addAuditManually').show();
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
<s:push value="#subHeading='InsureGUARD&trade;'"/>
<s:include value="conHeader.jsp" />

<table>
<tr><td>

<s:if test="requested.size > 0 || manuallyAddAudit">
<h3> Requested Insurance Policies</h3>
<table class="report noshade">
	<thead>
		<tr>
			<th>Policy Type</th>
			<th>Effective</th>
			<th>Expires</th>
			<th>Operator</th>
			<th>Status</th>
			<th>File</th>
		</tr>
	</thead>
	<s:iterator value="requested">

		<s:iterator value="value" status="stat">
		<tr>
			<s:if test="#stat.first">
				<td rowspan="<s:property value="value.size"/>">
					<a href="Audit.action?auditID=<s:property value="key.id" />"><s:property value="key.auditType.auditName" /></a>
					<s:if test="key.requestingOpAccount != null">
						<br />
						<span style="font-size:10px"> for <s:property value="key.requestingOpAccount.name"/></span>
					</s:if>
				</td>
				<td rowspan="<s:property value="value.size"/>"><s:date name="key.creationDate" format="M/d/yy" /></td>
				<td rowspan="<s:property value="value.size"/>"><s:date name="key.expiresDate" format="M/d/yy"/></td>
			</s:if>
			<td>
				<pics:permission perm="ManageOperators">
					<a href="FacilitiesEdit.action?id=<s:property value="operator.id"/>"><s:property value="operator.name"/></a>
				</pics:permission>
				<pics:permission perm="ManageOperators" negativeCheck="true">
					<s:property value="operator.name"/>
				</pics:permission>
			</td>
			<td class="Amber"><s:property value="status"/></td>
			<td style="height:30px">
				<s:if test="certificate != null">
					<a title="<s:property value="certificate.description"/>"
						href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="certificate.id"/>&button=download"
						target="_BLANK">
						<img src="images/icon_insurance.gif"/>
					</a>
				</s:if>
			</td>
		</tr>
		</s:iterator>
	</s:iterator>

	<s:if test="manuallyAddAudit">
		<s:if test="auditTypeName.size > 0">
			<tr>
				<td id="addAudit" colspan="6" class="center">
					<a href="#" onclick="showAddAudit(); return false;">Add New Policy</a>
				</td>
			</tr>
		</s:if>
		<tr id="addAuditManually" style="display: none;">
			<s:form method="post" id="form1" >
				<td colspan="6">
					<div>
						<input class="picsbutton positive" name="button" type="submit" value="Add"/>
					</div>
					<s:hidden name="id" value="%{id}"/>
					<s:select list="auditTypeName" name="selectedAudit" cssClass="pics"
						headerKey="" headerValue="- Select Policy Type -" listKey="id" listValue="auditName" />
				</td>
			</s:form>
		</tr>
	</s:if>
</table>
</s:if>

<s:if test="current.size > 0">
<br/>
<h3> Current Insurance Policies</h3>
<table class="report noshade">
	<thead>
		<tr>
			<th>Policy Type</th>
			<th>Effective</th>
			<th>Expires</th>
			<th>Operator</th>
			<th>Status</th>
			<th>File</th>
		</tr>
	</thead>
	<s:iterator value="current">
		<s:iterator value="value" status="stat">
		<tr>
			<s:if test="#stat.first">
				<td rowspan="<s:property value="value.size"/>">
					<a href="Audit.action?auditID=<s:property value="key.id" />"><s:property value="key.auditType.auditName" /></a>
					<s:if test="key.requestingOpAccount != null">
						<br />
						<span style="font-size:10px"> for <s:property value="key.requestingOpAccount.name"/></span>
					</s:if>
				</td>
				<td rowspan="<s:property value="value.size"/>"><s:date name="key.creationDate" format="M/d/yy" /></td>
				<td rowspan="<s:property value="value.size"/>"><s:date name="key.expiresDate" format="M/d/yy"/></td>
			</s:if>
			<td><s:property value="operator.name"/></td>
			<td class="<s:property value="status.color"/>"><s:property value="status"/></td>
			<td style="height:30px">
				<s:if test="certificate != null">
					<a title="<s:property value="certificate.description"/>"
						href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="certificate.id"/>&button=download">
						<img src="images/icon_insurance.gif"/>
					</a>
				</s:if>
			</td>
		</tr>
		</s:iterator>
	</s:iterator>
</table>
</s:if>

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
	<s:iterator value="expiredAudits">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:date name="creationDate" format="M/d/yy" /></td>
			<td><s:date name="expiresDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
</s:if>

<s:if test="others.size > 0">
<br/>
<h3> Other Non-Required Policies </h3>
<table class="report">
	<thead>
	<tr>
		<th>Type</th>
		<th>Created on</th>
	</tr>
	</thead>
	<s:iterator value="others">
		<tr>
			<td><a href="Audit.action?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></td>
			<td><s:date name="creationDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
</s:if>
</td>

<td style="width:2em">&nbsp;</td>

<td>
	<h3>Certificates</h3>
	<s:if test="certificates.size() > 0">
		<table class="report">
			<thead>
				<tr>
					<th>FileName</th>
					<th>Expiration Date</th>
					<th>View</th>
					<th>Edit</th>
					<th>Used By</th>
				</tr>
			</thead>
			<s:iterator value="certificates">
				<tr>
					<td><s:property value="description" /></td>
					<td><s:date name="expirationDate" format="M/d/yy" /></td>
					<td><a
						href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download"
						target="_BLANK"><img src="images/icon_insurance.gif" /></a></td>
					<td><s:if test="permissions.userId == createdBy.Id || permissions.admin">
						<a class="edit" href="#"
							onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />); return false;"
							title="Opens in new window (please disable your popup blocker)"">Edit</a>
					</s:if></td>
					<td>
						<table class="inner">
							<s:iterator value="caos">
								<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
								<tr>
									<td style="font-size:10px"><nobr><s:property value="audit.auditType.auditName"/></nobr></td>
									<td style="font-size:10px"><nobr><s:property value="operator.name"/></nobr></td>
									<td style="font-size:10px"><nobr><s:date name="audit.expiresDate" format="M/d/yy"/></nobr></td>
								</tr>
								</s:if>
							</s:iterator>
						</table>
					</td>
				</tr>
			</s:iterator>
		</table>
	</s:if>
	<div>
		<input type="button" class="picsbutton positive" value="Add File" onclick="showCertUpload(<s:property value="id" />, 0)" title="Opens in new window (please disable your popup blocker)"/>
	</div>
</td>
</tr>
</table>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
