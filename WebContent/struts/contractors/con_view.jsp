<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<s:include value="../reports/reportHeader.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/notes.css?v=20091231" />
<script type="text/javascript">
	function removeTag(tagId) {
		var data = {button: 'RemoveTag', tagId: tagId, id: <s:property value="id"/>};
		$('#conoperator_tags').html('<img src="images/ajax_process.gif"/>')
			.load('TagNameEditAjax.action', data, function(text, status) {
					if (status=='success')
						$(this).effect('highlight', {color: '#FFFF11'}, 1000);
				});
		return false;
	}
	
	function addTag() {
		var data = {button: 'AddTag', tagId: $('#tagName').val(), id: <s:property value="id"/>};
		$('#conoperator_tags').html('<img src="images/ajax_process.gif"/>')
			.load('TagNameEditAjax.action', data, function(text, status) {
					if (status=='success')
						$(this).effect('highlight', {color: '#FFFF11'}, 1000);
				});
		return false;
	}
</script>
</head>
<body>
<s:include value="conHeader.jsp" />
<s:if test="contractor.status.pending">
	<div class="alert">This contractor has not activated their account.</div>
</s:if>
<s:if test="contractor.status.deleted">
	<div class="alert">This contractor was deleted<s:if test="contractor.reason.length > 0"> 
			because of the following reason: <s:property value="contractor.reason"/></s:if>.
		<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
	</div>
</s:if>
<s:if test="contractor.status.deactivated">
	<div class="alert">This contractor was deactivated.
	<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
	</div>
</s:if>

<s:if test="contractor.acceptsBids">
	<s:if test="canUpgrade">
		<div class="info">This is a BID-ONLY Account and will expire on <strong><s:date name="contractor.paymentExpires" format="M/d/yyyy" /></strong><br/>
		Click <a href="ContractorView.action?id=<s:property value="id" />&button=Upgrade to Full Membership" class="picsbutton positive" onclick="return confirm('Are you sure you want to upgrade this account to a full membership? As a result a invoice will be generated for the upgrade and the flag color also will be affected based on the operator requirements.');">Upgrade to Full Membership</a> to continue working at your selected facilities.</div>
	</s:if>
	<s:else>
		<div class="alert">This is a BID-ONLY Contractor Account.</div>
	</s:else>
</s:if>
<s:elseif test="contractor.paymentOverdue && (permissions.admin || permissions.contractor)">
	<div class="alert">This contractor has an outstanding invoice due</div>
</s:elseif>
<s:if test="permissions.admin && !contractor.mustPayB">
	<div class="alert">This account has a lifetime free membership</div>
</s:if>

<div id="companyinfo">
	<div class="contact">
		<div class="left infobox">
			<div class="vcard">
				<div class="adr">
					<s:if test="logoWidth > 0">
						<img src="ContractorLogo.action?id=<s:property value="id"/>" width="<s:property value="logoWidth"/>" />
					</s:if>
					<p class="fn org"><s:property value="contractor.name" /></p>
					<s:if test="contractor.dbaName.length() > 0">
						<p class="fn org">DBA <s:property value="contractor.dbaName" /></p>
					</s:if>
					
					<p><span class="street-address"><s:property value="contractor.address" /></span>,
					<span class="locality"><s:property value="contractor.city" /></span>, 
					<span class="region"><s:property value="contractor.state.isoCode" /></span> 
					<span class="postal-code"><s:property value="contractor.zip" /></span></p>
					<p>[<a
						href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
						target="_blank">map</a>]</p>
				</div>
		 		<div class="telecommunications">
					<p class="tel">Main Phone: <span class="value"><s:property value="contractor.phone" /></span></p>
					<s:if test="contractor.fax" ><p class="tel">Main Fax: <span class="value"><s:property value="contractor.fax" /></span></p></s:if>
					<s:if test="contractor.webUrl.length() > 0" ><p class="url">Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></p></s:if>
					<p class="contact">Contact: <span class="value"><s:property value="contractor.primaryContact.name" /></span></p>
					<s:if test="contractor.primaryContact.phone.length() > 0"><p class="tel">&nbsp;&nbsp;Phone: <span class="value"><s:property value="contractor.primaryContact.phone" /></span></p></s:if>
					<s:if test="contractor.primaryContact.fax.length() > 0"><p class="tel">&nbsp;&nbsp;Fax: <span class="value"><s:property value="contractor.primaryContact.fax" /></span></p></s:if>
 					<s:if test="contractor.primaryContact.email.length() > 0"><p class="email">&nbsp;&nbsp;Email: <a href="mailto:<s:property value="contractor.primaryContact.email" />" class="value"><s:property value="contractor.primaryContact.email" /></a></p></s:if>
					<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false"><p class="web"><strong>
						<a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK">Company Brochure</a>
					</strong></p></s:if>
				</div>
			</div>
		</div>
	</div>
	<div class="left infobox">
<!-- TODO: add the VCard again -->
<!--		<div class="right" id="vcardimage"><a -->
<!--			href="http://suda.co.uk/projects/X2V/get-vcard.php?uri=http://www.albumcreative.com/picscss/index.html"><img -->
<!--			src="images/vcard.jpg" alt="image" width="130" height="38" /></a></div>-->
		PICS Contractor ID: <strong><s:property value="contractor.id" /></strong><br />
		Member Since: <strong><s:date name="contractor.membershipDate" format="M/d/yyyy" /></strong><br />
		PICS CSR: <strong><s:property value="contractor.auditor.name" /> / <s:property value="contractor.auditor.phone" /></strong><br />
		Email: <a href="mailto:<s:property value="contractor.auditor.email" />"><s:property value="contractor.auditor.email" /></a><br />
		<s:if test="contractor.webcam.trackingNumber.trim().length() > 0">
		Track Webcam: <a href="http://www.fedex.com/Tracking?tracknumber_list=<s:property value="contractor.webcam.trackingNumber"/>" target="_blank"><img src="images/icon_webcam.png"/></a><br/>
		</s:if>
		Risk Level: <strong><s:property value="contractor.riskLevel" /></strong><br />
		
		<pics:permission perm="ContractorDetails">
			<strong><a class="pdf" href="AuditPdfConverter.action?id=<s:property value="id"/>">Download PQF &amp; Annual Updates</a>
			</strong><br/>
		</pics:permission>
		
		Facilities:
		<ul style="list-style-type: none;">
			<s:iterator value="activeOperators">
			<li>
				<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"
				><s:property value="flagColor.smallIcon" escape="false" />
				<s:else><img src="images/icon_Flag.gif" width="10" height="12" border="0" title="Blank"/></s:else></a>
				<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"
					<s:if test="permissions.admin"> 
						title="<s:property value="operatorAccount.name" />: Waiting On '<s:property value="waitingOn"/>'"
						rel="OperatorQuickAjax.action?id=<s:property value="operatorAccount.id"/>"
						class="operatorQuick"
					</s:if>
					<s:else>
						title="Waiting On '<s:property value="waitingOn"/>'"
					</s:else>
					><s:property value="operatorAccount.name" /></a>
			</li>
			</s:iterator>
			<s:if test="!permissions.operator">
				<li>...<a href="ContractorFacilities.action?id=<s:property value="id" />">see Facilities</a></li>
			</s:if>
		</ul>
		<s:if test= "permissions.operator && (contractor.operatorTags.size() > 0 || operatorTags.size() > 0)">
			<fieldset class="form">
				<legend><span>Operator Tag Names: </span></legend>
				<ol><div id="conoperator_tags">
					<s:include value="contractorOperator_tags.jsp" />
					</div>
				</ol>
			</fieldset>
		</s:if>	
	</div>
</div>
<br clear="all" />
<div id="maincontainer"><s:property value="contractor.descriptionHTML" escape="false" /></div>

</body>
</html>
