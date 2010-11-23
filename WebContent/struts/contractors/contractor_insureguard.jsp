<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="/exception_handler.jsp"%>
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
<style type="text/css">
.Pending { color: #770 !important; }
.Current { color: #272 !important; }
</style>
</head>
<body>
<s:include value="conHeader.jsp" />

<table>
	<tr>
		<td>
			<s:iterator value="#{'Pending','Current','Expired','Other'}" var="stat">
				<s:if test="#stat.key != 'Expired' && status.get(#stat.key).size > 0">
					<h3><s:property value="#stat.key" /> Policies</h3>
					<table class="report">
						<thead>
							<tr>
								<th>Policy Type</th>
								<s:if test="#stat.key != 'Expired'">
									<th>Operator</th>
									<th>Status</th>
									<th>View</th>
								</s:if>
								<s:else>
									<th>Expired</th>
								</s:else>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="status.get(#stat.key).keySet()" var="audit">
								<s:set name="rowspan" value="%{status.get(#stat.key).get(#audit).size}" />
								<s:iterator value="status.get(#stat.key).get(#audit)" var="cao" status="s">
									<tr>
										<s:if test="#s.first">
											<td rowspan="<s:property value="#rowspan" />">
												<s:property value="#audit.auditType.auditName" />
												<br />
												<span style="font-size: 10px">
													for
													<s:if test="#audit.auditFor != null"><s:property value="#audit.auditFor"/></s:if>
													<s:else><s:date name="#audit.effectiveDate" format="MMM yyyy" /></s:else>
												</span>
											</td>
										</s:if>
										<td><s:property value="#cao.operator.name" /></td>
										<td><span class="<s:property value="#stat.key" />"><s:property value="#cao.status" /></span></td>
										<td class="center">
											<a title="<s:property value="caoCert.get(#cao).description"/>"
												href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="caoCert.get(#cao).id" />&button=download"
												target="_BLANK">
												<img src="images/icon_insurance.gif"/>
											</a>
										</td>
									</tr>
								</s:iterator>
							</s:iterator>
						</tbody>
					</table>
				</s:if>
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
					<s:iterator value="certificates" var="cert">
						<tr>
							<td><s:property value="description" /></td>
							<td><s:date name="expirationDate" format="M/d/yy" /></td>
							<td class="center"><a
								href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download"
								target="_BLANK"><img src="images/icon_insurance.gif" /></a></td>
							<td><s:if test="permissions.userId == createdBy.Id || permissions.admin">
								<a class="edit" href="#"
									onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />); return false;"
									title="Opens in new window (please disable your popup blocker)">Edit</a>
							</s:if></td>
							<td>
								<table class="inner">
									<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
										<s:iterator value="certCaos.get(#cert)" var="cao">
											<tr>
												<td style="font-size:10px"><nobr><s:property value="#cao.audit.auditType.auditName"/></nobr></td>
												<td style="font-size:10px"><nobr><s:property value="#cao.audit.auditFor" /> <s:date name="#audit.effectiveDate" format="MMM yyyy" /></nobr></td>
												<td style="font-size:10px"><nobr><s:property value="#cao.operator.name" /></nobr></td>
												<td style="font-size:10px"><nobr><s:date name="#cao.audit.expiresDate" format="M/d/yy"/></nobr></td>
											</tr>
										</s:iterator>
									</s:if>
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
