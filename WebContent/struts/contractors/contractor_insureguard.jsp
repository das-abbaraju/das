<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title>InsureGUARD&trade; for <s:property value="contractor.name" /></title>
<s:include value="../jquery.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=<s:property value="version"/>" />
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
	<tr>
		<td>
			<s:iterator value="policyOrder" id="status">
				<h3><s:property value="#status" /> Policies</h3>
				<table class="report">
					<thead>
						<tr>
							<th>Policy Type</th>
							<th>Operator</th>
							<th>Status</th>
							<th>View</th>
						</tr>
					</thead>
					<tbody>
						<s:iterator value="caoAudits" id="audit">
							<s:iterator value="caos.get(#status, #audit)" id="cao" status="stat">
								<tr>
									<s:if test="#stat.first">
										<td rowspan="<s:property value="caos.get(#status, #audit).size" />">
											<s:property value="#audit.auditType.auditName" />
										</td>
									</s:if>
									<td><s:property value="#cao.operator.name" /></td>
									<td><s:property value="#cao.status" /></td>
									<s:if test="#stat.first">
										<td rowspan="<s:property value="caos.get(#status, #audit).size" />">
											<s:iterator value="policyCert.get(#audit)" id="cert">
												test
												<a title="<s:property value="#cert.description"/>"
													href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="#cert.id"/>&button=download"
													target="_BLANK">
													<img src="images/icon_insurance.gif"/>
												</a>
											</s:iterator>
										</td>
									</s:if>
								</tr>
							</s:iterator>
						</s:iterator>
					</tbody>
				</table>
			</s:iterator>
		</td>
		<td style="padding-left: 2em;">
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
							<td class="center"><a
								href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download"
								target="_BLANK"><img src="images/icon_insurance.gif" /></a></td>
							<td><s:if test="permissions.userId == createdBy.Id || permissions.admin">
								<a class="edit" href="#"
									onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />); return fals
									title="Opens in new window (please disable your popup blocker)"">Edit</a>
							</s:if></td>
							<td>
								<table class="inner">
									<s:set name="idString" value="%{id + ''}" />
									<s:iterator value="certMap.get(#idString)" id="data">
										<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
											<s:iterator value="#data.audit.operators" id="cao">
												<tr>
													<td style="font-size:10px"><nobr><s:property value="#data.audit.auditType.auditName"/></nobr></td>
													<td style="font-size:10px"><nobr><s:property value="#cao.operator.name"/></nobr></td>
													<td style="font-size:10px"><nobr><s:date name="#data.audit.expiresDate" format="M/d/yy"/></nobr></td>
												</tr>
											</s:iterator>
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
