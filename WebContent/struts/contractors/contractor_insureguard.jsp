<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" errorPage="/exception_handler.jsp" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>
<head>
	<title>
		<s:text name="ConInsureGUARD.title">
			<s:param><s:text name="global.InsureGUARD" /></s:param>
			<s:param><s:property value="contractor.name" /></s:param>
		</s:text>
	</title>
	
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
		
		function showFileUpload(auditid, questionid) {
		    url = 'AuditDataUpload.action?auditData.audit.id=' + auditid + '&auditData.question.id=' + questionid + '&mode=Edit';
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
	
	<s:if test="currentPoliciesMap.size > 0">
		<h3><s:text name="ConInsureGUARD.CurrentPolicies" /></h3>
		
		<table class="report">
			<thead>
				<tr>
					<td><s:text name="Filters.label.PolicyType" /></td>
					
					<s:iterator value="@com.picsauditing.jpa.entities.AuditStatus@values()" var="stat">
						<s:if test="#stat.toString() != 'Expired'">
							<td><s:property value="#stat" /></td>
						</s:if>
					</s:iterator>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="currentPoliciesMap.keySet()" var="audit">
					<tr>
						<td>
							<a href="Audit.action?auditID=<s:property value="#audit.id"/>">
								<s:text name="Audit.auditFor" >
									<s:param value="%{#audit.auditType.name}" />
									<s:param value="%{#audit.auditFor != null && #audit.auditFor.length() > 0 ? 1 : 0}" />
									<s:param value="%{#audit.auditFor != null && #audit.auditFor.length() > 0 ? #audit.auditFor : #audit.effectiveDateLabel}" />
								</s:text>
							</a>
						</td>
						
						<s:iterator value="@com.picsauditing.jpa.entities.AuditStatus@values()" var="stat">
							<s:if test="#stat.toString() != 'Expired'">
								<td>
									<table class="inner">
										<s:iterator value="currentPoliciesMap.get(#audit)" var="cao">
											<tr>
												<td style="font-size:10px">
													<s:if test="#cao.status.toString() == #stat.toString()">
														<nobr><s:property value="#cao.operator.name" /></nobr>
													</s:if>
												</td>
											</tr>
										</s:iterator>
									</table>
								</td>
							</s:if>
						</s:iterator>
					</tr>
				</s:iterator>
			</tbody>
		</table>
		<br />
	</s:if>
	
	<s:if test="expiredPoliciesMap.size > 0">
		<h3><s:text name="ConInsureGUARD.ExpiredPolicies" /></h3>
		
		<table class="report">
			<thead>
				<tr>
					<td>
						<s:text name="Filters.label.PolicyType" />
					</td>
					<td>
						<s:text name="Filters.label.ExpiredDate" />
					</td>
				</tr>
			</thead>
			<tbody>
				<s:iterator value="expiredPoliciesMap.keySet()" var="expiredPolicy">
					<tr>
						<td>
							<a href="Audit.action?auditID=<s:property value="#expiredPolicy.id"/>">
								<s:text name="Audit.auditFor" >
									<s:param value="%{#expiredPolicy.auditType.name}" />
									<s:param value="%{#expiredPolicy.auditFor != null && #expiredPolicy.auditFor.length() > 0 ? 1 : 0}" />
									<s:param value="%{#expiredPolicy.auditFor != null && #expiredPolicy.auditFor.length() > 0 ? #expiredPolicy.auditFor : #expiredPolicy.effectiveDateLabel}" />
								</s:text>
							</a>
						</td>
						<td>
							<s:date name="#expiredPolicy.expiresDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}"/>
						</td>
					</tr>
				</s:iterator>
			</tbody>
		</table>
	</s:if>
	<br />
	
	<table>
		<tr>
			<td>
				<s:iterator value="certTypes" var="stat">
					<s:if test="certificatesMap.row(#stat).size > 0">
						<h3>
							<s:text name="ConInsureGUARD.NumCertificates">
								<s:param><s:property value="#stat" /></s:param>
							</s:text>
						</h3>
						
						<table class="report">
							<thead>
								<tr>
									<td>
										<s:text name="global.Filename" />
									</td>
									<td>
										<s:text name="global.ExpirationDate" />
									</td>
									<td>
										<s:text name="global.View" />
									</td>
									<td>
										<s:text name="global.Edit" />
									</td>
									
									<s:if test="#stat != 'Uploaded'">
										<td>
											<s:text name="ConInsureGUARD.UsedBy" />
										</td>
									</s:if>
								</tr>
							</thead>
							<tbody>
								<s:iterator value="certificatesMap.row(#stat).keySet()" var="cert">
									<tr>
										<td>
											<s:property value="description" />
										</td>
										<td class="center">
											<s:date name="expirationDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
										</td>
										<td class="center">
											<a href="CertificateUpload.action?id=<s:property value="contractor.id"/>&certID=<s:property value="id"/>&button=download" target="_BLANK">
												<img src="images/icon_insurance.gif" />
											</a>
										</td>
										<td>
											<s:if test="permissions.userId == createdBy.Id || permissions.admin">
												<a class="edit" href="#" onclick="showCertUpload(<s:property value="contractor.id"/>, <s:property value="id" />); return false;" title="Opens in new window (please disable your popup blocker)">
													<s:text name="global.Edit" />
												</a>
											</s:if>
										</td>
										<s:if test="#stat != 'Uploaded'">
											<td>
												<s:if test="!permissions.operatorCorporate || permissions.insuranceOperatorID == operator.id">
													<s:property value="certificatesMap.get(#stat, #cert)" />
												</s:if>
											</td>
										</s:if>
									</tr>
								</s:iterator>
							</tbody>
						</table>
					</s:if>
					<br />
				</s:iterator>
				
				<div>
					<input type="button" class="picsbutton positive" onclick="showCertUpload(<s:property value="id" />, 0)" title="Opens in new window (please disable your popup blocker)" value="<s:text name="Audit.AddFile" />" />
				</div>
			</td>
		</tr>
	</table>

	<s:if test="wcbFiles.size > 0">
	<br />
	<h3><s:text name="ConInsureGUARD.WcbFiles" /></h3>
	<table class="report">
		<thead>
			<tr>
				<td>
					<s:text name="Filters.label.PolicyType" />
				</td>
				<td>
					<s:text name="ConInsureGUARD.Policy" />
				</td>
				<td class="center">
					<s:text name="global.ExpirationDate" />
				</td>
				<td class="center">
					<s:text name="global.View" />
				</td>
				<td>
					<s:text name="global.Edit" />
				</td>
				<td>
					<s:text name="ConInsureGUARD.UsedBy" />
				</td>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="wcbFiles" var="wcb">
				<tr>
					<td><s:property value="#wcb.auditName" /></td>
					<td><s:property value="#wcb.title" /></td>
					<td class="center"><s:date name="#wcb.expires" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" /></td>
					<td class="center">
						<a href="DownloadAuditData.action?auditID=<s:property value="#wcb.auditId"/>&auditData.question.id=<s:property value="#wcb.questionId"/>" target="_BLANK">
							<img src="images/icon_insurance.gif" />
						</a>
					</td>
					<td class="center">
							<a class="edit" href="#" onclick="showFileUpload(<s:property value="#wcb.auditId"/>, <s:property value="#wcb.questionId" />); return false;" title="Opens in new window (please disable your popup blocker)">
								<s:text name="global.Edit" />
							</a>
					</td>
					<td>
						<s:iterator value="#wcb.operators" var="op">
							<s:property value="#op.name" /><br />
						</s:iterator>
					</td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
	<br/>
	</s:if>
		
	<div id="notesList">
		<s:include value="../notes/account_notes_embed.jsp"></s:include>
	</div>
	
</body>