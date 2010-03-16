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
div.co_flag {
	float: left;
	text-align: center;
	height: 100%;
	width: 10%;
	margin: 2%;
}
div.co_select {
	clear: left;
}
</style>

</head>
<body>

<s:include value="conHeader.jsp"/>
<table>
<tr>
<td>
	<!-- Operator Flag Status -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contractor Status at <s:property value="co.operatorAccount.name"/>
			</div>
			<div class="panel_content">
				<s:if test="co != null">
					<div class="co_flag">
						<s:property value="co.flagColor.bigIcon" escape="false"/> <br />
						<s:property value="co.flagColor"/> <br />
						<a href="#">Force</a>
					</div>
					<div class="co_problems">
						Problems:
						<ul>
							<s:iterator value="problems">
								<li><s:property value="critieria.label"/></li>
							</s:iterator>
						</ul>
					</div>
				</s:if>
				<div class="co_select">
					<nobr>
					Viewing Dashboard as: 
					<s:select list="contractor.operators" listKey="operatorAccount.id" listValue="operatorAccount.name" name="opID"
						headerKey="" headerValue=" - Operator - "
							onchange="location.href='ContractorView.action?id=%{id}&opID='+this.value"/>.
					</nobr>
				</div>
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
	<!-- AuditGUARD -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				AuditGUARD
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
	<!-- Statistics -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Statistics
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
</td>

<td width="15px"></td>

<td>
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
				<s:iterator value="activeOperatorsMap">
				<ul style="list-style-type: none; float: left;">
					<s:iterator value="value">
					<li>
						<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />">
							<s:property value="flagColor.smallIcon" escape="false" />
						</a>
						<a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"
							<s:if test="permissions.admin"> 
								title="<s:property value="operatorAccount.name" />: Waiting On '<s:property value="waitingOn"/>'"
								rel="OperatorQuickAjax.action?id=<s:property value="operatorAccount.id"/>"
								class="operatorQuick"
							</s:if>
							<s:else>
								title="Waiting On '<s:property value="waitingOn"/>'"
							</s:else>
							>
							<s:property value="operatorAccount.name" />
						</a>
					</li>
					</s:iterator>
				</ul>
				</s:iterator>
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
</td>
</tr>
</table>

</body>

</html>