<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<head>
	<title>
		<s:property value="contractor.name" />
	</title>
</head>
<body>
	<div id="tabs">
		<ul>
			<li>
				<a href="#tabs-general">
					<s:text name="global.General" />
				</a>
			</li>
			<li>
				<a href="#tabs-contact">
					<s:text name="global.Contact" />
				</a>
			</li>
			<li>
				<a href="#tabs-trades">
					<s:text name="global.Trades" />
				</a>
			</li>
			<li>
				<a href="#tabs-facilities">
					<s:text name="global.Facilities" />
				</a>
			</li>
			<li>
				<a href="#tabs-audits">
					<s:text name="Audit.header.Documents" />
				</a>
			</li>
			<s:if test="permissions.admin || permissions.contractor">
				<li>
					<a href="#tabs-membership">
						<s:text name="ContractorQuick.Membership" />
					</a>
				</li>
			</s:if>
		</ul>
		<div id="tabs-general">
			<s:if test="contractor.dbaName.length() > 0">
				<label>
					<s:text name="ContractorAccount.dbaName.short" />:
				</label>
				<s:property value="contractor.dbaName" />
				<br />
			</s:if>
			<label>
				<s:text name="ContractorAccount.id" />
			</label>
			<s:property value="contractor.id" />
			<br />
			<pics:permission perm="PicsScore">
				<label>
					<s:text name="ContractorAccount.score" />:
				</label>
				<s:property value="contractor.score" />
				<br />
			</pics:permission>
			<label>
				<s:text name="global.SafetyRisk" />:
			</label>
			<s:text name="%{contractor.safetyRisk.i18nKey}" />
			<br />
			<s:if test="contractor.materialSupplier && contractor.productRisk != null">
				<label>
					<s:text name="global.ProductRisk" />:
				</label>
				<s:text name="%{contractor.productRisk.i18nKey}" />
				<br />
			</s:if>
			<label>
				<s:text name="global.Location" />:
			</label>
			<s:property value="contractor.city" />,
			<s:if test="contractor.country.hasCountrySubdivisions">
				<s:property value="contractor.countrySubdivision.name" />
			</s:if>
			<s:if test="permissions.country != contractor.country.isoCode || !contractor.country.hasCountrySubdivisions">
				<s:property value="contractor.country.name" />
			</s:if>
			<br />
			<s:if test="contractor.operatorTags.size() > 0">
				<label>
					<s:text name="ContractorQuick.OperatorTags" />:
				</label>
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
			<a href="ContractorNotes.action?id=<s:property value="contractor.id" />">
				<s:text name="global.Notes" />
			</a>
			<s:if test="permissions.admin || permissions.contractor">
				|
				<a href="ContractorEdit.action?id=<s:property value="contractor.id" />">
					<s:text name="button.Edit" />
				</a>
			</s:if>
			<pics:permission perm="SwitchUser">
				<s:iterator value="contractor.users">
					|
					<a href="Login.action?button=login&switchToUser=<s:property value="id"/>">
						<s:text name="ContractorQuick.LoginAs">
							<s:param value="%{name}" />
						</s:text>
					</a>
				</s:iterator>
			</pics:permission>
		</div>
		<div id="tabs-contact">
			<label>
				<s:text name="global.ContactPrimary" />:
			</label>
			<s:property value="contractor.primaryContact.name"/>
			<br />
			<label>
				<s:text name="User.phone" />:
			</label>
			<s:property value="contractor.primaryContact.phone"/>
			<br />	
			<label>
				<s:text name="User.fax" />:
			</label>
			<s:property value="contractor.primaryContact.fax"/>
			<br />	
			<label>
				<s:text name="User.email" />:
			</label>
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
						<th>
							<s:text name="ContractorApproval.WorkStatus" />
						</th>
					</pics:permission>
				</tr>
				</thead>
				<s:iterator value="activeOperators">
				<tr>
					<td>
						<s:property value="flagColor.smallIcon" escape="false" />
					</td>
					<td>
						<s:url action="ContractorFlag" var="con_op_flag_link">
							<s:param name="id" value="%{contractor.id}" />
							<s:param name="opID" value="%{operatorAccount.id}" />
						</s:url>
						<a href="${con_op_flag_link}">
							<s:property value="operatorAccount.name" />
						</a>
					</td>
					<td>
						<s:text name="%{waitingOn.i18nKey}"/>
					</td>
					<pics:permission perm="ContractorApproval">
						<td>
							<s:property value="workStatus"/>
						</td>
					</pics:permission>
				</tr>
				</s:iterator>
			</table>
			<s:if test="permissions.admin || permissions.contractor">
				<a class="edit" href="ContractorFacilities.action?id=<s:property value="contractor.id" />">
					<s:text name="ContractorQuick.EditFacilities" />
				</a>
			</s:if>
		</div>
		<div id="tabs-audits">
			<table class="report">
				<thead>
					<tr>
						<th>
							<s:text name="Audit.header.Documents" />
						</th>
						<th>
							<s:text name="global.Status" />
						</th>
					</tr>
				</thead>
				<s:iterator value="activeAudits">
					<tr>
						<td>
							<a href="Audit.action?auditID=<s:property value="id" />">
								<s:if test="auditFor.length() > 0">
									<s:property value="auditFor" />
								</s:if>
								<s:property value="auditType.name" />
							</a>
						</td>
						<td>
							<s:iterator value="getCaoStats(permissions).keySet()" id="status">
								<logic:notEmpty name="getCaoStatus(permissions).get(#status)">
									<s:property value="getCaoStats(permissions).get(#status)" />
									<s:text name="%{#status.i18nKey}" />
								</logic:notEmpty>
								<br />					
							</s:iterator>
						</td>
					</tr>
				</s:iterator>
			</table>
		</div>
		<s:if test="permissions.admin || permissions.contractor">
			<div id="tabs-membership">
				<s:iterator value="operatorTags">
					<s:property value="tag"/>
				</s:iterator>
				<label>
					<s:text name="ContractorQuick.PicsMembership" />:
				</label>
				<s:property value="contractor.status"/>
				<br />
				<label>
					<s:text name="ContractorQuick.Membership" />:
				</label>
				<br />
				<table>
					<s:iterator value="contractor.fees.keySet()" var="feeClass">
						<s:if test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
							<tr>
								<td colspan="2">
									<s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:
								</td>
								<td class="right">
									<s:property value="contractor.country.currency.symbol" />
									<s:property value="contractor.fees.get(#feeClass).currentAmount" />
								</td>
								<td>
									<s:property value="contractor.currency"/>
								</td>
							</tr>
						</s:if>
					</s:iterator>
				</table>
				<label>
					<s:text name="InvoiceDetail.Balance" />:
				</label>
				<s:property value="contractor.country.currency.symbol" />
				<s:property value="contractor.balance" />
				<br />
				<label>
					<s:text name="ContractorView.MemberSince" />:
				</label>
				<s:date name="contractor.membershipDate" format="%{@com.picsauditing.util.PicsDateFormat@Iso}" />
				<br />
				<a href="BillingDetail.action?id=<s:property value="contractor.id" />">
					<s:text name="BillingDetail.title" />
				</a>
			</div>
		</s:if>
	</div>
	
	<script type="text/javascript">
	$(function() {
		$("#tabs").tabs({
			event: 'mouseover'
		});
		
		$('a.trade').each(function() {
			$(this).attr('href', 'ContractorTrades.action?id=<s:property value="id"/>');
		});
	});
	</script>
</body>