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
	max-width: 300px;
	/* IE Image max-width */
	width: expression(this.width > 300 ? 300 : true);
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
			<div class="panel_content">
				
			</div>
		</div>
	</div>
</div>

</body>

</html>