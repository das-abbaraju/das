<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<%@ page language="java" errorPage="exception_handler.jsp"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=20091105" />
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
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
<s:if test="!contractor.activeB">
	<div id="alert">This contractor is not active.
	<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
	</div>
</s:if>
<s:if test="contractor.acceptsBids">
	<s:if test="canUpgrade">
		<div id="info">This is a BID-ONLY Account and will expire on <strong><s:date name="contractor.paymentExpires" format="M/d/yyyy" /></strong><br/>
		Click <a href="ContractorView.action?id=<s:property value="id" />&button=Upgrade to Full Membership" class="picsbutton positive" onclick="return confirm('Are you sure you want to upgrade this account to a full membership? As a result a invoice will be generated for the upgrade and the flag color also will be affected based on the operator requirements.');">Upgrade to Full Membership</a> to continue working at your selected facilities.</div>
	</s:if>
	<s:else>
		<div id="alert">This is a BID-ONLY Contractor Account.</div>
	</s:else>
</s:if>
<s:elseif test="contractor.paymentOverdue && (permissions.admin || permissions.contractor)">
	<div id="alert">This contractor has an outstanding invoice due</div>
</s:elseif>
<s:if test="permissions.admin && !contractor.mustPayB">
	<div id="alert">This account has a lifetime free membership</div>
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
					<span class="region"><s:property value="contractor.state" /></span> 
					<span class="postal-code"><s:property value="contractor.zip" /></span></p>
					<p>[<a
						href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
						target="_blank">map</a>]</p>
				</div>
		 		<div class="telecommunications">
					<p class="contact">Contact: <span class="value"><s:property value="contractor.contact" /></span></p>
					<p class="tel">Phone: <span class="value"><s:property value="contractor.phone" /></span></p>
					<s:if test="contractor.phone2" ><p class="tel">Other Phone: <span class="value"><s:property value="contractor.phone2" /></span></p></s:if>
					<s:if test="contractor.fax" ><p class="tel">Fax: <span class="value"><s:property value="contractor.fax" /></span></p></s:if>
 					<p class="email">Email: <strong><a href="mailto:<s:property value="contractor.email" />" class="value"><s:property value="contractor.email" /></a></strong></p>
					<s:if test="contractor.webUrl.length() > 0" ><p class="url">Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></p></s:if>
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
		PICS CSR: <strong><s:property value="contractor.auditor.name" /></strong><br />
		Risk Level: <strong><s:property value="contractor.riskLevel" /></strong><br />
		
		<pics:permission perm="ContractorDetails">
			<strong><a class="pdf" href="AuditPdfConverter.action?id=<s:property value="id"/>">Download PQF & Annual Updates</a>
			</strong><br/>
		</pics:permission>
		
		Facilities:
		<ul style="list-style-type: none;">
			<s:iterator value="activeOperators">
			<li>
				<s:if test="flag != null">
					<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="flag.flagColor.smallIcon" escape="false" /></a>
				</s:if>
				<s:else>
					<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><img src="images/icon_Flag.gif" width="10" height="12" border="0" title="Blank"/></a>
				</s:else>
				<a title="Waiting On : <s:property value="flag.waitingOn"/>" href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a>
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
