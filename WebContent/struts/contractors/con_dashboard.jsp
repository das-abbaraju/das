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
	width: 40px;
	margin: 10px 20px 10px 5px;
}
div.co_problems {
	float: left;
}
div.co_select {
	clear: left;
	border-top: 1px solid #cecece;
	margin: 2px;
	padding: 2px;
	text-align: center;
}
div.flagData {
	float: left;
	margin: 2px;
}
span.other_operator {
	float: left;
}
</style>

</head>
<body>

<s:include value="conHeader.jsp"/>
<table>
<tr>
<td style="vertical-align:top">
	<!-- Operator Flag Status -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contractor Status <s:if test="co != null">at <s:property value="co.operatorAccount.name"/></s:if>
			</div>
			<div class="panel_content">
				<s:if test="co != null">
					<div class="co_flag">
						<p><s:property value="co.flagColor.bigIcon" escape="false"/></p>
						<p><s:property value="co.flagColor"/></p>
						<p><a href="#">Force</a></p>
					</div>
					<div class="co_problems">
						<s:if test="problems.size() > 0">
							<p>Problems:
								<ul style="list-style: none; margin-left: 10px;">
									<s:iterator value="problems">
										<li><s:property value="flag.smallIcon" escape="false"/> <s:property value="criteria.label"/></li>
									</s:iterator>
								</ul>
							</p>
						</s:if>
						<p>Waiting On:
							<s:property value="co.waitingOn"/>
						</p>
						<p>Works In:
							<s:property value="contractor.state.name"/>
						</p>
						<p>Last Login:
							<s:property value="getFuzzyDate(contractor.lastLogin)"/>
						</p>
					</div>
				</s:if>
				<s:if test="!permissions.operatorCorporate">
				<div class="co_select">
					Viewing Dashboard as: 
					<s:select list="activeOperators" listKey="operatorAccount.id" listValue="operatorAccount.name" name="opID"
						headerKey="" headerValue=" - Operator - "
							onchange="location.href='ContractorView.action?id=%{id}&opID='+this.value"/>
				</div>
				</s:if>
				<div class="clear"></div>
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
				
				<div class="clear"></div>
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
				
				<div class="clear"></div>
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
				
				<div class="clear"></div>
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
				
				<div class="clear"></div>
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
				<div class="clear" style="height: 0px; overflow: hidden"></div>
				<s:iterator value="flaggableData">
					<div class="flagData">
						<strong><s:property value="key"/>:</strong>
						<ul style="list-style: none">
						<s:iterator value="value">
							<li><s:property value="flag.smallIcon" escape="false"/> <s:property value="criteria.label"/></li>
						</s:iterator>
						</ul>
					</div>
				</s:iterator>
				<script type="text/javascript">
					$(function() {
						$('div.flagData').equalWidth();
					});
				</script>
				<div class="clear"></div>
			</div>
		</div>
	</div>
</td>

<td width="15px"></td>

<td style="vertical-align:top">
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
				<div class="clear"></div>
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
				<div class="clear"></div>
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
				<s:iterator value="activeOperators">
					<span class="other_operator">
						<nobr>
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
						</nobr>
					</span>
				</s:iterator>
				<script type="text/javascript">
					$(function() {
						$('.other_operator').equalWidth();
					});
				</script>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	<s:if test="permissions.contractor">
	<!-- Open Tasks -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Open Tasks
			</div>
			<div class="panel_content" id="con_tasks">
				<div class="inprogress"></div>
				<script type="text/javascript">
					$(function() {
							$('#con_tasks').load('ContractorTasksAjax.action?id=<s:property value="id"/>');
						}
					);
				</script>
			</div>
		</div>
	</div>
	</s:if>
</td>
</tr>
</table>

</body>

</html>