<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
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
.Incomplete { color: #900 !important; }
.NotApplicable { color: #555 !important; }
</style>
</head>
<body>
<s:include value="conHeader.jsp" />

<table>
	<tr>
		<td style="padding-left: 2em;">
			<s:iterator value="@com.picsauditing.jpa.entities.AuditStatus@values()" var="stat">
				<s:if test="policiesMap.get(#stat).size > 0">
					<h3><s:property value="#stat" /> Policies</h3>
					<table class="report">
						<thead>
							<tr>
								<td>Policy Type</td>
								<td>Operator</td>
								<td>Status</td>
								<td>View</td>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="policiesMap.get(#stat).keySet()" var="audit">
								<s:iterator value="policiesMap.get(#stat).get(#audit)" var="cao" status="s">
								<tr>
									<td rowspan="<s:property value="#rowspan" />">
										<a href="Audit.action?auditID=<s:property value="#audit.id"/>">
											<s:property value="#audit.auditType.name" /> '<s:property value="getAuditForYear(#audit.effectiveDate)" />
										</a>
										<br />
										<span style="font-size: 10px">
											<s:if test="#stat == 'Expired'">
												Expired on <s:date name="#audit.expiresDate" format="M/d/yy" />
											</s:if>
											<s:else>
												<s:date name="#audit.effectiveDate" format="MMM yyyy" />
											</s:else>
										</span>
									</td>
									<td><s:property value="#cao.operator.name" /></td>
									<!-- <td><s:property value="#cao.status" /></td> -->
									<td><span class="<s:property value="#stat" /> <s:property value="#cao.status" />"><s:property value="#cao.status" /></span></td>
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
			<s:iterator value="certTypes" var="stat">
				<s:if test="certificatesMap.get(#stat).size > 0">
					<h3><s:property value="#stat" /> Certificates</h3>
					<table class="report">
						<thead>
							<tr>
								<td>File Name</td>
								<td>Expiration Date</td>
								<td>View</td>
								<td>Edit</td>
								<s:if test="#stat != 'Uploaded'">
									<td>Used By</td>
								</s:if>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="certificatesMap.get(#stat).keySet()" var="cert">
								<tr>
									<td><s:property value="description" /></td>
									<td class="center"><s:date name="expirationDate" format="M/d/yy" /></td>
									<td class="center"><a
										href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download"
										target="_BLANK"><img src="images/icon_insurance.gif" /></a></td>
									<td><s:if test="permissions.userId == createdBy.Id || permissions.admin">
										<a class="edit" href="#"
											onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />); return false;"
											title="Opens in new window (please disable your popup blocker)">Edit</a>
									</s:if></td>
									<s:if test="#stat != 'Uploaded'">
										<td>
											<table class="inner">
												<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
													<s:iterator value="certificatesMap.get(#stat).get(#cert)" var="cao">
														<tr>
															<td style="font-size:10px"><nobr><s:property value="#cao.audit.auditType.name"/></nobr></td>
															<td style="font-size:10px"><nobr><s:property value="#cao.audit.auditFor" /> <s:date name="#audit.effectiveDate" format="MMM yyyy" /></nobr></td>
															<td style="font-size:10px"><nobr><s:property value="#cao.operator.name" /></nobr></td>
															<td style="font-size:10px"><nobr><s:date name="#cao.audit.expiresDate" format="M/d/yy"/></nobr></td>
														</tr>
													</s:iterator>
												</s:if>
											</table>
										</td>
									</s:if>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</s:if>
			</s:iterator>
			<div>
				<input type="button" class="picsbutton positive" value="Add File" onclick="showCertUpload(<s:property value="id" />, 0)" title="Opens in new window (please disable your popup blocker)"/>
			</div>
		</td>
	</tr>
</table>

<div id="notesList"><s:include value="../notes/account_notes_embed.jsp"></s:include></div>

</body>
</html>
