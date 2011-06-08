<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<s:include value="../jquery.jsp" />
<s:include value="../reports/reportHeader.jsp"/>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/trades.css?v=<s:property value="version"/>" />
<link rel="stylesheet" type="text/css" media="screen" href="css/dashboard.css?v=<s:property value="version"/>" />
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

	function limit(id, pat) {
		var data = $('#' + id).text();
		var size = 500;
		var count = index = prev = 0;
		while(count < 15){
			prev = index;
			index = data.indexOf(pat, prev+pat.length);
			if(index!=-1)
				count++;
			else break;
		}
		if(count>=15){
			if(prev<size)
				size = prev;
		}

		if (data.length > size) {
			var data1 = data.substring(0,size).replace(/\n/gi, "<br>") + '<span id="' + id + '_ext">...<br> <a href="#" onclick="$(\'#'
				+ id + '_more\').show(); $(\'#' + id + '_ext\').hide(); return false;" style="font-weight: normal;"'
				+ '>Show more</a><br><br></span>';
			var data2 = '<span id="' + id + '_more" style="display: none;">' + data.substring(size, data.length).replace(/\n/gi, "<br>")
				+ ' <a href="#" onclick="$(\'#' + id + '_ext\').show(); $(\'#' + id
				+ '_more\').hide(); return false;" style="font-weight: normal;">Hide</a><br><br></span>';
			data = data1 + data2;
		};

		$('#' + id).html(data);
	}

	function startWatch() {
		$('#contractorWatch').html('<img src="images/ajax_process.gif" alt="Loading" />Adding Contractor watch...');
		$.get('ContractorViewAjax.action', {button: 'Start Watch', id: <s:property value="contractor.id" />}, function (output) {
			$('#contractorWatch').html('You are watching this contractor. <a href="#" onclick="stopWatch(); return false;">Stop Watching</a>')
				.effect('highlight', {color: '#FFFF11'}, 1000);
		});
	}

	function stopWatch() {
		$('#contractorWatch').html('<img src="images/ajax_process.gif" alt="Loading" />Removing Contractor Watch...');
		$.get('ContractorViewAjax.action', {button: 'Stop Watch', id: <s:property value="contractor.id" />}, function (output) {
			$('#contractorWatch').html('<a href="#" onclick="startWatch(); return false;" class="watch">Watch This Contractor</a>')
				.effect('highlight', {color: '#FFFF11'}, 1000);
		});
	}

	function wireTradeClueTips() {
		$("#trade-cloud a.trade").cluetip({
			clickThrough: true,
			ajaxCache: false,
			closeText: "<img src='images/cross.png' width='16' height='16' />",
			hoverIntent: {interval: 200},
			arrows: true,
			dropShadow: false,
			width: 500,
			cluetipClass: 'jtip',
			ajaxProcess: function(data) {
				data = $(data).not('meta, link, title');
				return data;
			}
		});
	}

	$(document).ready(function() {
		wireTradeClueTips();
		limit('description', '\n');
		$('.reloadPage').live('click', function(){
			location.reload();
		});
		$('a.tradeInfo').live('click',function() {
			$($(this).attr('href')).toggle();
		});
		$('a.trade').each(function() {
			$(this).attr('href', 'ContractorTrades.action?id=<s:property value="id"/>');
		});
	});
</script>
<style>
div#opTagAjax{
	/*background: #F9F9F9;
	border: 2px solid #012142;
	padding: 3px;
	display: none;
	width: 80%;
	margin-left: auto;
	margin-right: auto;*/
}
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
tr.hurdle {
	background-color:Cornsilk;
	display: none;
}
table.report tr.hurdle td {
	font-size: 11px;
}
</style>
</head>
<body>
<s:include value="conHeader.jsp"/>
<s:if test="permissions.contractor">
	<div class="info"><s:text name="%{scope}.ContractorDashboard.Description" /></div>
</s:if>
<s:if test="permissions.admin">
	<s:if test="contractor.hasPastDueInvoice()">
		<div class="alert"><s:text name="%{scope}.ContractorDashboard.HasPastDueInvoice" /></div>
	</s:if>
</s:if>

<table>
<tr>
<td style="vertical-align:top; width: 48%">
	<!-- Operator Flag Status -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				<s:text name="%{scope}.ContractorDashboard.ContractorStatus">
					<s:param><s:if test="co != null"> at <s:property value="co.operatorAccount.name"/></s:if></s:param>
				</s:text>
			</div>
			<div class="panel_content">
				<s:if test="contractor.status.pending">
					<div class="alert"><s:text name="%{scope}.ContractorDashboard.StatusPending" /></div>
				</s:if>
				<s:if test="contractor.soleProprietor">
					<div class="alert"><s:text name="%{scope}.ContractorDashboard.SoleProprietor" />
					<a href="#" onclick="return false;" class="cluetip help" rel="#cluetip_sole_sync" title="Sole Proprietor"></a>
						<div style="display: none;" id="cluetip_sole_sync"><s:text name="%{scope}.ContractorDashboard.SoleProprietor.fieldhelp" /></div>
					</div>
				</s:if>
				<s:if test="contractor.status.deleted">
					<div class="alert">This contractor was deleted<s:if test="contractor.reason.length > 0">
							because of the following reason: <s:property value="contractor.reason"/></s:if>.
						<s:if test="contractor.lastPayment != null">They last paid on <s:property value="contractor.lastPayment"/>.</s:if>
					</div>
				</s:if>
				<s:if test="contractor.acceptsBids">
					<s:if test="canUpgrade">
						<div class="info">This is a Bid Only Account. You will have to upgrade to a Full Membership if you intend to work at any of your selected facilities.<br/>
						<div style="margin-top: 7px;"><a href="ContractorView.action?id=<s:property value="id" />&button=Upgrade to Full Membership" class="picsbutton positive" onclick="return confirm('Are you sure you want to upgrade this account to a full membership? As a result a invoice will be generated for the upgrade and the flag color also will be affected based on the operator requirements.');">Upgrade to Full Membership</a></div></div>
					</s:if>
					<s:else>
						<div class="alert">This is a Bid Only Contractor Account.</div>
					</s:else>
				</s:if>
				<s:if test="permissions.admin && !contractor.mustPayB && contractor.status.active">
					<div class="alert">This account has a lifetime free membership</div>
				</s:if>

				<s:if test="co != null">
					<s:if test="co.operatorAccount.approvesRelationships.toString() == 'Yes'">
						<s:if test="co.workStatusPending">
							<div class="alert">The operator has not approved this contractor yet.</div>
						</s:if>
						<s:elseif test="co.workStatusRejected">
							<div class="alert">The operator did not approve this contractor.</div>
						</s:elseif>
					</s:if>

					<div class="co_flag">
						<s:if test="permissions.corporate && opID == permissions.accountId">
							<p><s:property value="co.flagColor.bigIcon" escape="false"/></p>
							<p><s:if test="co.flagColor.clear">Not Applicable</s:if>
								<s:else><s:property value="co.flagColor"/></s:else></p>
						</s:if>
						<s:else>
							<p><a href="ContractorFlag.action?id=<s:property value="id"/>&opID=<s:property value="opID"/>"><s:property value="co.flagColor.bigIcon" escape="false"/></a></p>
							<p><a href="ContractorFlag.action?id=<s:property value="id"/>&opID=<s:property value="opID"/>">
							<s:if test="co.flagColor.clear">Not Applicable</s:if>
								<s:else><s:property value="co.flagColor"/></s:else></a></p>
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
						<s:if test="contractor.accountLevel.full && contractor.balance > 0"><p>Balance: $<s:property value="format(contractor.balance)"/></p></s:if>
					</s:if>
					<s:if test="problems.categories.size() > 0">
						<p><s:text name="%{scope}.ContractorDashboard.Problems" />:
							<ul style="margin-left: 10px;">
								<s:iterator value="problems.categories" id="probcat">
									<s:iterator value="problems.getCriteria(#probcat)" id="probcrit">
										<li><s:property value="problems.getWorstColor(#probcrit).smallIcon" escape="false"/> <s:property value="label"/> <s:property value="getCriteriaLabel(#probcrit.id)"/></li>
									</s:iterator>
								</s:iterator>
							</ul>
						</p>
					</s:if>
					<s:if test="co != null">
						<p><s:text name="%{scope}.ContractorDashboard.WaitingOn" />:
							<s:property value="co.waitingOn"/>
						</p>
					</s:if>
					<!--
						<p>
							<s:if test="contractor.requiresOQ">Operator Qualification: Enabled</s:if>
						</p>
						<p>
							<s:if test="contractor.requiresCompetencyReview">HSE Competency Review: Enabled</s:if>
						</p>
					-->
					<p><s:text name="%{scope}.ContractorDashboard.LastLogin" />:
						<s:property value="getFuzzyDate(contractor.lastLogin)"/>
					</p>
					<s:if test="activeOperators.size() > 1">
						<p><a href="#all">Locations</a>:
							<s:property value="activeOperators.size()"/>
							<s:if test="flagCounts.size() > 0">
								(<s:iterator value="flagCounts" status="stat"><s:property value="value"/> <s:property value="key.smallIcon" escape="false"/><s:if test="!#stat.last">, </s:if></s:iterator>)
							</s:if>
						</p>
					</s:if>
					<pics:permission perm="ContractorWatch" type="Edit">
						<p id="contractorWatch">
							<s:if test="watched">
								You are watching this contractor. <a href="#" onclick="stopWatch(); return false;">Stop Watching</a>
							</s:if>
							<s:else>
								<a class="watch" href="#" onclick="startWatch(); return false;">Watch This Contractor</a>
							</s:else>
						</p>
					</pics:permission>
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
						<a href="Audit.action?auditID=<s:property value="id"/>"><s:property value="auditType.name"/>
						<s:if test="auditFor != null"> <s:property value="auditFor"/></s:if><s:else><s:date name="effectiveDate" format="MMM yyyy" /></s:else></a>
						<s:if test="key == 'InsureGUARD'">
							<s:property value="getSynopsis(co.operatorAccount)"/>
						</s:if>
						<s:else>
							<s:property value="synopsis"/>
						</s:else>
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
				<span style="float: right;">
					<a href="#" id="hurdleLinkShow" onclick="$('tr.hurdle').show(); $('#hurdleLinkShow').hide(); $('#hurdleLinkHide').show(); return false;">Show Hurdle Rates</a>
					<a href="#" id="hurdleLinkHide" onclick="$('tr.hurdle').hide(); $('#hurdleLinkHide').hide(); $('#hurdleLinkShow').show(); return false;" style="display: none">Hide Hurdle Rates</a>
				</span>
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

						<tr <s:if test="#rateType.startsWith('P:')">class="hurdle"</s:if>>
							<s:if test="#rateType.startsWith('P:')">
								<td style="padding-left: 10px;"><s:property value="#rateType.substring(2)" escape="false"/></td>
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
				<s:text name="%{scope}.ContractorDashboard.FlaggableData" />
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
				<s:text name="%{scope}.ContractorDashboard.ContractorInfo" />
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
				<pics:permission perm="PicsScore">
				<p>Score:
					<strong>
						<s:property value="contractor.score" />
					</strong>
				</p>
				</pics:permission>
				<p><s:text name="%{scope}.ContractorDashboard.MemberSince" />:
					<strong>
						<strong><s:date name="contractor.membershipDate" format="M/d/yyyy" /></strong>
					</strong>
				</p>
				<p>PICS CSR:
					<strong><s:property value="contractor.auditor.name" /> / <s:property value="contractor.auditor.phone" /> / </strong>
					<a href="mailto:<s:property value="contractor.auditor.email"/>" class="email"><s:property value="contractor.auditor.email"/></a>
				</p>
				<p><s:text name="global.SafetyRisk" />:
					<strong><s:text name="%{contractor.safetyRisk.i18nKey}" /></strong>
				</p>
				<s:if test="contractor.materialSupplier && contractor.productRisk != null">
					<p><s:text name="global.ProductRisk" />: <strong><s:text name="%{contractor.productRisk.i18nKey}" /></strong></p>
				</s:if>
				<s:if test="hasOperatorTags">
					<s:if test= "contractor.operatorTags.size() > 0 || operatorTags.size() > 0">
						<div><span>Operator Tag Names: </span>
							<div id="conoperator_tags">
							<s:include value="contractorOperator_tags.jsp" />
							</div>
						</div>
					</s:if>
				</s:if>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	<!-- Contact Info -->
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				<s:text name="%{scope}.ContractorDashboard.ContactInfo" />
			</div>
			<div class="panel_content">
				<p><s:text name="%{scope}.ContractorDashboard.Address" />: [<a
					href="http://www.mapquest.com/maps/map.adp?city=<s:property value="contractor.city" />&state=<s:property value="contractor.state" />&address=<s:property value="contractor.address" />&zip=<s:property value="contractor.zip" />&zoom=5"
					target="_blank"><s:text name="%{scope}.ContractorDashboard.ShowMap" /></a>]<br/>
					<span class="street-address"><s:property value="contractor.address" /></span><br />
					<span class="locality"><s:property value="contractor.city" /></span>,
					<span class="region"><s:property value="contractor.state.isoCode" /></span>
					<span class="postal-code"><s:property value="contractor.zip" /></span> <br />
				</p>
				<div class="telecommunications">
					<p class="tel"><s:text name="%{scope}.ContractorDashboard.MainPhone" />: <span class="value"><s:property value="contractor.phone" /></span></p>
					<s:if test="!isStringEmpty(contractor.fax)"><p class="tel">Main Fax: <span class="value"><s:property value="contractor.fax" /></span></p></s:if>
					<s:if test="contractor.webUrl.length() > 0"><p class="url">Web site: <strong><a href="http://<s:property value="contractor.webUrl" />" class="value" target="_blank"><s:property value="contractor.webUrl" /></a></strong></p></s:if>
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
				<s:text name="%{scope}.ContractorDashboard.DescriptionHeader" />
			</div>
			<div class="panel_content">
				<s:if test="showLogo">
					<img class="contractor_logo" src="ContractorLogo.action?id=<s:property value="id"/>"/>
				</s:if>
				<span id="description"><s:property value="contractor.descriptionHTML" /></span>
				<s:if test="@com.picsauditing.util.Strings@isEmpty(contractor.brochureFile) == false">
					<p class="web"><strong>
						<a href="DownloadContractorFile.action?id=<s:property value="id" />" target="_BLANK">Company Brochure</a>
					</strong></p>
				</s:if>
				<s:if test="contractor.trades.size() > 0">
					<s:include value="../trades/contractor_trade_cloud.jsp"/>
				</s:if>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	<!-- All Locations -->
	<div class="panel_placeholder">
		<div class="panel" id="all">
			<div class="panel_header">
				<s:text name="%{scope}.ContractorDashboard.AllLocations" />
			</div>
			<div class="panel_content">
				<s:iterator value="activeOperatorsMap">
					<ul style="float: left">
					<s:iterator value="value">
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
					</s:iterator>
					</ul>
				</s:iterator>
				<div class="clear"></div>
			</div>
		</div>
	</div>
	<s:if test="permissions.admin || permissions.contractor">
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
					});
				</script>
			</div>
		</div>
	</div>
	</s:if>
	<div class="panel_placeholder">
		<div class="panel">
			<div class="panel_header">
				<s:text name="%{scope}.ContractorDashboard.SynchronizeContractor" /> <span style="float: right;"><a href="#" onclick="return false;" class="cluetip help" rel="#cluetip_sync" title="Synchronize Contractor"></a></span>
				<div id="cluetip_sync">Click this button to update the PQF, audits, flags, and other requirements for this contractor. You need to click this button only when requirements have changed and you want to see the results immediately.</div>
			</div>
			<div class="panel_content" style="text-align: center;">
				<s:form id="form_sync">
					<s:hidden name="id" />
					<s:hidden name="button" value="Synchronize Contractor" />
					<input type="submit" class="picsbutton" onclick="$(this).attr('disabled', true); $('#form_sync').submit();"
						style="margin: 5px auto;" value="Synchronize" />
					<s:if test="contractor.lastRecalculation != null">
						<br />Last synchronized <s:date name="contractor.lastRecalculation" nice="true" />
					</s:if>
				</s:form>
			</div>
		</div>
	</div>
</td>
</tr>
</table>

</body>

</html>