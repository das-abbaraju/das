<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>
</head>
<body>
<h1><s:property value="contractor.name" />
<span class="sub">Contractor Details</span></h1>

<div id="internalnavcontainer">
<ul id="navlist">
	<li><a href="ContractorView.action?id=<s:property value="id" />" class="current">Details</a></li>
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

<div id="companyinfo">
	<div class="contact">
		<div class="left info">
			<div class="vcard">
				<div class="adr">
					<p class="fn org"><s:property value="contractor.name" /></p>
					<p><span class="street-address"><s:property value="contractor.address" /></span>,
					<span class="locality"><s:property value="contractor.city" /></span>, 
					<span class="region"><s:property value="contractor.state" /></span>, 
					<span class="postal-code"><s:property value="contractor.zip" /></span></p>
					<p>[<a
						href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
						target="_blank">map</a>]</p>
				</div>
		 		<div class="telecommunications">
					<p class="contact">Contact: <span class="value"><s:property value="contractor.contact" /></span></p>
					<p class="tel">Phone: <span class="value"><s:property value="contractor.phone" /></span></p>
					<p class="tel">Other Phone: <span class="value"><s:property value="contractor.phone2" /></span></p>
					<p class="tel">Fax: <span class="value"><s:property value="contractor.fax" /></span></p>
 					<p class="email">Email: <strong><a href="mailto:<s:property value="contractor.email" />" class="value"><s:property value="contractor.email" /></a></strong></p>
					<p class="url">Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></p>
					<p class="web"><strong><a href="servlet/showpdf?id=<s:property value="id" />&file=brochure" class="ext" target="_blank">Company Brochure</a></strong></p>
				</div>
			</div>
		</div>
	</div>
	<div class="left info">
		PICS Contractor ID: <strong><s:property value="contractor.luhnId" /></strong><br />
		Risk Level: <strong><s:property value="contractor.riskLevel" /></strong><br />
		Facilities:
		<ul>
			<s:iterator value="contractor.operators">
			<li><s:property value="operatorAccount.name" /></li>
			</s:iterator>
			<li>...<a href="con_selectFacilities.jsp?id=<s:property value="id" />">see facilities</a></li>
		</ul>
		<div class="right" id="vcardimage"><a 
			href="http://suda.co.uk/projects/X2V/get-vcard.php?uri=http://www.albumcreative.com/picscss/index.html"><img 
			src="images/vcard.jpg" alt="image" width="130" height="38" /></a></div>
	</div>
</div>
<br clear="all" />
<div id="maincontainer"><s:property value="contractor.descriptionHTML" escape="false" /></div>

</body>
</html>
