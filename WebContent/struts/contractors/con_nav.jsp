<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<s:property value="id" />">Details</a></li>
	<s:if test="permissions.auditor">
	<li><a class="blueMain" href="contractor_list_auditor.jsp">Return to
	Contractors List</a></li>
	</s:if>	
	<s:if test="permissions.contractor">
	<li><a href="contractor_edit.jsp?id=<s:property value="id" />">Edit</a></li>
	</s:if>
	<pics:permission perm="AllContractors">
	<li><a href="accounts_edit_contractor.jsp?id=<s:property value="id" />">Edit</a></li>
	</pics:permission>
	<pics:permission perm="InsuranceCerts">
	<li><a href="contractor_upload_certificates.jsp?id=<s:property value="id" />">InsureGuard</a></li>
	</pics:permission>
	<s:if test="permissions.operator">
	<li><a href="con_redFlags.jsp?id=<s:property value="id" />">Flag Status</a></li>
	</s:if>
	<s:if test="%{value != permissions.operator}">
	<li><a href="con_selectFacilities.jsp?id=<s:property value="id" />">Facilities</a></li>
	</s:if>
	<s:if test="permissions.contractor">
	<li><a href="con_viewForms.jsp?id=<s:property value="id" />">Forms & Docs</a></li>
	</s:if>
	<li><a href="ConAuditList.action?id=<s:property value="id" />">Audits</a></li>
	<s:iterator value="activeAudits">
		<li><a href="pqf_view.jsp?auditID=<s:property value="id" />"><s:property value="auditType.auditName" /></a></li>
	</s:iterator>
</ul>
</div>
