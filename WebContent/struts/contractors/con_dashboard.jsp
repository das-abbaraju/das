<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" />'s Dashboard</title>

<s:include value="../jquery.jsp" />
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css" />

<style>
img.contractor_logo {
	float: left;
	max-width: 180px;
	/* IE Image max-width */
	width: expression(this.width > 180 ? 180 : true);
}
</style>

</head>
<body>

<s:include value="conHeader.jsp"/>

<div class="column">
	<!-- Operator Flag Status -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contractor Status at <s:property value="operator.name"/>
			</div>
			<div class="panel_content">
				
			</div>
		</div>
	</div>
	<!-- DocuGUARD -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				DocuGUARD
			</div>
			<div class="panel_content">
				
			</div>
		</div>
	</div>
	<!-- InsureGUARD -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				InsureGUARD
			</div>
			<div class="panel_content">
				
			</div>
		</div>
	</div>
	<!-- Regulatory Data -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Regulatory Data
			</div>
			<div class="panel_content">
				
			</div>
		</div>
	</div>
	<!-- Flaggable Data -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Flaggable Data
			</div>
			<div class="panel_content">
				
			</div>
		</div>
	</div>
</div>

<div class="column">
	<!-- Contact Info -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contact Info
			</div>
			<div class="panel_content">
				<p>PICS CSR: 
					<strong>
						<s:property value="contractor.auditor.name" />
						<s:property value="contractor.auditor.phone" />
						<a href="mailto:<s:property value="contractor.auditor.email"/>"><s:property value="contractor.auditor.email"/></a>
					</strong>
				</p>
				<p>Primary Contact: 
					<strong>
						<s:property value="contractor.primaryContact.name" />
						<s:property value="contractor.primaryContact.phone" />
						<a href="mailto:<s:property value="contractor.primaryContact.email"/>"><s:property value="contractor.primaryContact.email"/></a>
					</strong>
				</p>
				<p><span class="street-address"><s:property value="contractor.address" /></span><br />
					<span class="locality"><s:property value="contractor.city" /></span>, 
					<span class="region"><s:property value="contractor.state.isoCode" /></span> 
					<span class="postal-code"><s:property value="contractor.zip" /></span> <br />
					[<a
					href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
					target="_blank">Show Map</a>]
				</p>
				<s:if test="contractor.webUrl.length() > 0"><p class="url"><strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></p></s:if>
				<p>Risk Level: <strong><s:property value="contractor.riskLevel"/></strong></p>
				<p>Primary Industry: <strong><s:property value="contractor.industry"/> (<s:property value="contractor.naics.code"/>)</strong></p>
				<p>Services Performed: <s:iterator value="servicesPerformed" status="stat"><strong><s:property value="question.question"/></strong><s:if test="!#stat.last">, </s:if></s:iterator></p>
				<p>Tags: <s:iterator value="contractor.operatorTags" status="stat"><strong><s:property value="tag.tag"/></strong><s:if test="!#stat.last">, </s:if></s:iterator></p>
			</div>
		</div>
	</div>
	<!-- Description -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Description
			</div>
			<div class="panel_content">
				<img class="contractor_logo" src="ContractorLogo.action?id=<s:property value="id"/>"/>
				<s:property value="contractor.description"/>
			</div>
		</div>
	</div>
	<!-- Other Locations -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Other Locations
			</div>
			<div class="panel_content">
				
			</div>
		</div>
	</div>
	<!-- Open Tasks -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Open Tasks
			</div>
			<div class="panel_content" id="con_tasks">
				<script type="text/javascript">
					$(function() {
							$('#con_tasks').load('ContractorTasksAjax.action?id=<s:property value="id"/>');
						}
					);
				</script>
			</div>
		</div>
	</div>
</div>

</body>

</html>