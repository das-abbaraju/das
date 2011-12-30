<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<html>
<head>
<title><s:property value="contractor.name" /></title>

<script>
$(document).ready(function() {
	$('a.trade').each(function() {
		$(this).attr('href', 'ContractorTrades.action?id=<s:property value="id"/>');
	});
});
</script>
</head>
<body>
<div id="tabs">
	<ul>
		<li><a href="#tabs-general">General</a></li>
		<li><a href="#tabs-contact">Contact</a></li>
		<li><a href="#tabs-trades">Trades</a></li>
		<li><a href="#tabs-facilities">Facilities</a></li>
		<li><a href="#tabs-audits">Documents</a></li>
		<s:if test="permissions.admin || permissions.contractor">
			<li><a href="#tabs-membership">Membership</a></li>
		</s:if>
	</ul>
	<div id="tabs-general">
		<s:if test="contractor.dbaName.length() > 0">
			<label>DBA:</label>
			<s:property value="contractor.dbaName" />
			<br />
		</s:if>
		<label>PICS #:</label>
		<s:property value="contractor.id" />
		<br />
		<!-- 
			<s:if test="contractor.requiresOQ">
				<label>Operator Qualification:</label> Enabled
			<br />
			</s:if>
			<s:if test="contractor.requiresCompetencyReview">
				<label>HSE Competency Review:</label> Enabled
			<br />
			</s:if>
		-->
		<pics:permission perm="PicsScore">
		<label><s:text name="ContractorAccount.score" />:</label>
		<s:property value="contractor.score" />
		<br />
		</pics:permission>
		<label><s:text name="global.SafetyRisk" />:</label>
		<s:text name="%{contractor.safetyRisk.i18nKey}" />
		<br />
		<s:if test="contractor.materialSupplier && contractor.productRisk != null">
			<label><s:text name="global.ProductRisk" />:</label>
			<s:text name="%{contractor.productRisk.i18nKey}" />
			<br />
		</s:if>
		<label>Location:</label>
		<s:property value="contractor.city" />,
		<s:if test="contractor.country.hasStates">
			<s:property value="contractor.state.name" />
		</s:if>
		<s:if test="permissions.country != contractor.country.isoCode || !contractor.country.hasStates">
			<s:property value="contractor.country.name" />
		</s:if>
		<br />
		<s:if test="contractor.operatorTags.size() > 0">
		<label>Operator Tags:</label> 
		<s:iterator value="tagsViewableByUser">
			<s:if test="permissions.admin">
				<s:property value="tag.tag"/> - <s:property value="tag.operator.name"/> <br/>
			</s:if>			
			<s:if test="permissions.operator">
				<s:property value="tag.tag"/> <br/>	
			</s:if>
		</s:iterator>
		<br />
		</s:if>
		<a href="ContractorNotes.action?id=<s:property value="contractor.id" />">Notes</a>
		<s:if test="permissions.admin || permissions.contractor">
			| <a href="ContractorEdit.action?id=<s:property value="contractor.id" />"><s:text name="button.Edit" /></a>
		</s:if>
		<pics:permission perm="SwitchUser">
			<s:iterator value="contractor.users">
				| <a href="Login.action?button=login&switchToUser=<s:property value="id"/>">Login as <s:property value="name" /></a>
			</s:iterator>
		</pics:permission>
	</div>
	<div id="tabs-contact">
		<label><s:text name="global.ContactPrimary" />:</label>
		<s:property value="contractor.primaryContact.name"/>
		<br />
		<label><s:text name="User.phone" />:</label>
		<s:property value="contractor.primaryContact.phone"/>
		<br />	
		<label><s:text name="User.fax" />:</label>
		<s:property value="contractor.primaryContact.fax"/>
		<br />	
		<label><s:text name="User.email" />:</label>
		<s:property value="contractor.primaryContact.email"/>
		<br />	
	</div>
	<div id="tabs-trades">
		<s:set var="hideTradeCloudInstructions" value="true" />
		<s:include value="../trades/contractor_trade_cloud.jsp"/>
	</div>
	<div id="tabs-facilities">
		<table class="report">
			<thead>
			<tr>
				<th></th>
				<th><s:text name="global.Operator"/></th>
				<th><s:text name="global.WaitingOn"/></th>
				<pics:permission perm="ContractorApproval">
					<th>Work Status</th>
				</pics:permission>
			</tr>
			</thead>
			<s:iterator value="activeOperators">
			<tr>
				<td><s:property value="flagColor.smallIcon" escape="false" /></td>
				<td><a href="ContractorFlag.action?id=<s:property value="contractor.id" />&opID=<s:property value="operatorAccount.id" />"><s:property value="operatorAccount.name" /></a></td>
				<td><s:text name="%{waitingOn.i18nKey}"/></td>
				<pics:permission perm="ContractorApproval">
					<td><s:property value="workStatus"/></td>
				</pics:permission>
			</tr>
			</s:iterator>
		</table>
		<s:if test="permissions.admin || permissions.contractor">
			<a class="edit" href="ContractorFacilities.action?id=<s:property value="contractor.id" />">Edit Facilities</a>
		</s:if>
	</div>
	<div id="tabs-audits">
		<table class="report">
			<thead>
				<tr>
					<th>Document</th>
					<th><s:text name="global.Status" /></th>
				</tr>
			</thead>
			<s:iterator value="activeAudits">
				<tr>
					<td><a href="Audit.action?auditID=<s:property value="id" />">
						<s:if test="auditFor.length() > 0"><s:property value="auditFor" /></s:if>
						<s:property value="auditType.name" /></a></td>
						<td><s:iterator value="getCaoStats(permissions).keySet()" id="status">
							<nobr><s:if test="getCaoStats(permissions).get(#status) > 1"><s:property value="getCaoStats(permissions).get(#status)"/></s:if>
							 <s:property value="#status"/></nobr><br/>							
						</s:iterator></td>
				</tr>
			</s:iterator>
		</table>
	</div>
	<s:if test="permissions.admin || permissions.contractor">
		<div id="tabs-membership">
			<s:iterator value="operatorTags"><s:property value="tag"/> </s:iterator>
			<label>PICS Membership:</label><s:property value="contractor.status"/>
			<br />
			<label>Membership:</label><br />
			<table>
				<s:iterator value="contractor.fees.keySet()" var="feeClass">
					<s:if test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
						<tr><td colspan="2"><s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:&nbsp;</td><td class="right"><s:property value="contractor.currencyCode.symbol" /><s:property value="contractor.fees.get(#feeClass).currentAmount" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
					</s:if>
				</s:iterator>
			</table>
			<label>Balance:</label>
			$<s:property value="contractor.balance" />
			<br />
			<label>Member Since:</label>
			<s:date name="contractor.membershipDate" format="M/d/yyyy" />
			<br />
			<a href="BillingDetail.action?id=<s:property value="contractor.id" />">Billing Details</a>
		</div>
	</s:if>
</div>

<script type="text/javascript">
$(function() {
	$("#tabs").tabs({
		event: 'mouseover'
	});
});
</script>
</body>
</html>
