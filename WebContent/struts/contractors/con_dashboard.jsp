<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<s:include value="../jquery.jsp" />
<s:include value="../reports/reportHeader.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css" />
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
<style>
img.contractor_logo {
	float: left;
	padding: 20px;
	max-width: 180px;
	/* IE Image max-width */
	width: expression(this.width > 180 ? 180 : true);
}
div.co_flag {
	float: left;
	text-align: center;
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
.hide_services {
	display: none;
}
ul {
	list-style: none;
}
#conoperator_tags {
	padding: 10px;
}
</style>

</head>
<body>

<s:include value="conHeader.jsp"/>
<s:if test="permissions.contractor">
	<div class="info">This is your profile page that the operators see when they look up your account. 
	Use the dropdown below to view the page from your operator's point of view.</div>
</s:if>

<table>
<tr>
<td style="vertical-align:top; width: 48%">
	<!-- Operator Flag Status -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contractor Status <s:if test="co != null">at <s:property value="co.operatorAccount.name"/></s:if>
			</div>
			<div class="panel_content">
				<s:if test="contractor.status.pending">
					<div class="alert">This contractor has not activated their account.</div>
				</s:if>
				<s:if test="contractor.status.deleted">
					<div class="alert">This contractor was deleted<s:if test="contractor.reason.length > 0"> 
							because of the following reason: <s:property value="contractor.reason"/></s:if>.
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
				<s:if test="permissions.admin && !contractor.mustPayB && contractor.status.active">
					<div class="alert">This account has a lifetime free membership</div>
				</s:if>

				<s:if test="co != null">
					<div class="co_flag">
						<s:if test="permissions.corporate">
							<p><s:property value="co.flagColor.bigIcon" escape="false"/></p>
							<p><s:property value="co.flagColor"/></p>
						</s:if>
						<s:else>
							<p><a href="ContractorFlag.action?id=<s:property value="id"/>&opID=<s:property value="opID"/>"><s:property value="co.flagColor.bigIcon" escape="false"/></a></p>
							<p><a href="ContractorFlag.action?id=<s:property value="id"/>&opID=<s:property value="opID"/>"><s:property value="co.flagColor"/></a></p>
						</s:else>
					<s:if test="co.forcedFlag">
						<div class="co_force" style="border: 2px solid #A84D10; background-color: #FFC; padding: 10px;">
							Manual Force Flag <s:property value="co.forceFlag.smallIcon" escape="false" /> until <s:date name="co.forceEnd" format="MMM d, yyyy" />
						</div>
					</s:if>
					</div>
				</s:if>
				<div class="co_problems">
					<s:if test="permissions.admin">
						<p>Account Status: <strong><s:property value="contractor.status"/></strong></p>
						<s:if test="!contractor.acceptsBids && contractor.balance > 0"><p>Balance: $<s:property value="format(contractor.balance)"/></p></s:if>
					</s:if>
					<s:if test="problems.categories.size() > 0">
						<p>Problems:
							<ul style="margin-left: 10px;">
								<s:iterator value="problems.categories" id="probcat">
									<s:iterator value="problems.getCriteria(#probcat)" id="probcrit">
										<li><s:property value="problems.getWorstColor(#probcrit).smallIcon" escape="false"/> <s:property value="label"/> <s:property value="audit.auditFor"/></li>
									</s:iterator>
								</s:iterator>
							</ul>
						</p>
					</s:if>
					<s:if test="co != null">
						<p>Waiting On:
							<s:property value="co.waitingOn"/>
						</p>
					</s:if>
					<p>Last Login:
						<s:property value="getFuzzyDate(contractor.lastLogin)"/>
					</p>
				</div>
				<s:if test="activeOperators.size() > 1">
				<div class="co_select nobr">
					Select Operator: 
					<s:select list="activeOperators" listKey="operatorAccount.id" listValue="operatorAccount.name" name="opID"
						headerKey="" headerValue=" - Operator - "
							onchange="location.href='ContractorView.action?id=%{id}&opID='+this.value"/>
				</div>
				</s:if>
				<div class="clear"></div>
			</div>
			
		</div>
	</div>
	<s:iterator value="#{'DocuGUARD': docuGUARD, 'AuditGUARD': auditGUARD, 'InsureGUARD': insureGUARD}">
	<s:if test="value.size() > 0">
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				<s:property value="key"/>
			</div>
			<div class="panel_content">
				<ul>
					<pics:permission perm="ContractorDetails">
					<s:if test="key == 'DocuGUARD'">
						<li><strong><a class="pdf" href="AuditPdfConverter.action?id=<s:property value="id"/>">Download PQF &amp; Annual Updates</a></strong></li></s:if>
					</pics:permission>
				<s:iterator value="value">
					<li>
						<a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditType.auditName"/><s:if test="auditFor != null"> <s:property value="auditFor"/></s:if></a>
						<s:property value="synopsis"/>
					</li>
				</s:iterator>
				</ul>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	</s:if>
	</s:iterator>
	<s:if test="oshaDisplay.hasData">
	<!-- Statistics -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Statistics
			</div>
			<div class="panel_content">
				<table class="report">
					<thead>
						<tr>
							<td></td>
							<s:iterator value="oshaDisplay.auditForSet" id="auditFor">
								<td><s:property value="#auditFor"/></td>
							</s:iterator>
						</tr>
					</thead>
					<s:iterator value="oshaDisplay.rateTypeSet" id="rateType">
						<tr>
							<s:if test="#rateType.startsWith('P:')">
								<td style="padding-left: 10px"><s:property value="#rateType.substring(2)" escape="false"/></td>
							</s:if>
							<s:else>
								<td><s:property value="#rateType" escape="false"/></td>
							</s:else>
							<s:iterator value="oshaDisplay.auditForSet" id="auditFor">
								<td><s:property value="oshaDisplay.getData(#rateType, #auditFor)" escape="false"/></td>
							</s:iterator>
						</tr>
					</s:iterator>
				</table>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	</s:if>
	
	<s:if test="criteriaList.categories.size() > 0">
	<!-- Flaggable Data -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Flaggable Data
			</div>
			<div class="panel_content">
				<div class="clear" style="height: 0px; overflow: hidden"></div>
				<s:iterator value="criteriaList.categories" id="datacat">
					<s:if test="#datacat != 'Insurance Criteria'">
					<div class="flagData">
						<strong><s:property value="#datacat"/></strong>
						<ul>
						<s:iterator value="criteriaList.getCriteria(#datacat)" id="datacrit">
							<li><span><s:property value="criteriaList.getWorstColor(#datacrit).smallIcon" escape="false"/> <s:property value="label"/></span></li>
						</s:iterator>
						</ul>
					</div>
					</s:if>
				</s:iterator>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	</s:if>
</td>

<td width="15px"></td>

<td style="vertical-align:top; width: 48%">
	<!-- Contractor Info -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contractor Info
			</div>
			<div class="panel_content">
				<h4><s:property value="contractor.name" />
					<s:if test="contractor.dbaName.length() > 0">
						<br/>DBA <s:property value="contractor.dbaName" />
					</s:if>
				</h4>
				<p>PICS Contractor ID: 
					<strong>
						<s:property value="contractor.id" />
					</strong>
				</p>
				<p>Member Since: 
					<strong>
						<strong><s:date name="contractor.membershipDate" format="M/d/yyyy" /></strong>
					</strong>
				</p>
				<p>PICS CSR: 
					<strong><s:property value="contractor.auditor.name" /> / <s:property value="contractor.auditor.phone" /> / </strong>
					<a href="mailto:<s:property value="contractor.auditor.email"/>" class="email"><s:property value="contractor.auditor.email"/></a>
				</p>
				<p>Risk Level: <strong><s:property value="contractor.riskLevel"/></strong></p>
				<s:if test= "permissions.operator && (contractor.operatorTags.size() > 0 || operatorTags.size() > 0)">
					<div><span>Operator Tag Names: </span>
						<div id="conoperator_tags">
						<s:include value="contractorOperator_tags.jsp" />
						</div>
					</div>
				</s:if>	
				<div class="clear"></div>
			</div>
		</div>
	</div>	
	<!-- Contact Info -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				Contact Info
			</div>
			<div class="panel_content">
				<p>Address: [<a
					href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
					target="_blank">Show Map</a>]<br/>
					<span class="street-address"><s:property value="contractor.address" /></span><br />
					<span class="locality"><s:property value="contractor.city" /></span>, 
					<span class="region"><s:property value="contractor.state.isoCode" /></span> 
					<span class="postal-code"><s:property value="contractor.zip" /></span> <br />
				</p>
				<div class="telecommunications">
					<p class="tel">Main Phone: <span class="value"><s:property value="contractor.phone" /></span></p>
					<s:if test="contractor.fax" ><p class="tel">Main Fax: <span class="value"><s:property value="contractor.fax" /></span></p></s:if>
					<s:if test="contractor.webUrl.length() > 0" ><p class="url">Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></p></s:if>
					<s:iterator value="contractor.getUsersByRole('ContractorAdmin')">
					<p class="contact"><s:if test="contractor.primaryContact.id == id">Primary </s:if>Contact: <span class="value"><s:property value="name" /></span></p>
					<p class="tel">&nbsp;&nbsp;Email: <a href="mailto:<s:property value="email" />" class="email"><s:property value="email" /></a>
						<s:if test="phone.length() > 0"> / Phone: <s:property value="phone" /></s:if>
						<s:if test="fax.length() > 0"> / Fax: <s:property value="fax" /></s:if>
					</p>
					</s:iterator>
				</div>
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
				<s:if test="showLogo">
					<img class="contractor_logo" src="Download.action?fileType=logos&id=<s:property value="id"/>"/>
				</s:if>
				<p>Primary Industry: <strong><s:property value="contractor.industry"/></strong></p>
				<s:property value="contractor.descriptionHTML" escape="false" />
				<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
					<p class="web"><strong>
						<a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK">Company Brochure</a>
					</strong></p>
				</s:if>
				<p id="services">Services Performed: 
					<s:iterator value="servicesPerformed" status="stat"><s:if test="#stat.count <= 5"><strong><s:property value="question.question"/></strong><s:if test="!#stat.last">, </s:if></s:if></s:iterator>
					<s:if test="servicesPerformed.size() > 5">
						<span class="hide_services">
							<s:iterator value="servicesPerformed" status="stat"><s:if test="#stat.count > 5"><strong><s:property value="question.question"/></strong><s:if test="!#stat.last">, </s:if></s:if></s:iterator>
						</span>
						<a href="#" id="more_services">Show more...</a>
						<script type="text/javascript">
							$(function() {
									$('a#more_services').click(function(e) {
											if ($(this).text() == 'Show more...') {
												$('.hide_services').show();
												$(this).text('Hide');
											}
											else {
												$('.hide_services').hide();
												$(this).text('Show more...');
											}

											e.preventDefault();
										}
									);
								}
							);
						</script>
					</s:if>
				</p>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	<s:if test="activeOperators.size() > 1 || permissions.admin">
		<!-- Other Locations -->
		<div class="panel_placeholder">
			<div class="panel">
				<div class="panel_header">
					All Locations
				</div>
				<div class="panel_content">
					<s:iterator value="activeOperatorsMap">
						<ul>
						<s:iterator value="value">
							<s:if test="permissions.accountId != operatorAccount.id">
								<li>
								<span class="other_operator">
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
								</span>
								</li>
							</s:if>
						</s:iterator>
						</ul>
					</s:iterator>
					<div class="clear"></div>
				</div>
			</div>
		</div>
	</s:if>
	<s:if test="permissions.admin">
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