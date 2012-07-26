<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib" %>

<%-- URLS --%>
<s:url action="ContractorPricing" var="contractor_pricing">
	<s:param name="con">
		${contractor.id}
	</s:param>
</s:url>

<s:if test="currentOperators.size() == 0">
	<div
		class="alert"
		style="width: 80%;">
		<s:text name="ContractorFacilities.NotLinked">
			<s:param>
				${contractor.name}
			</s:param>
		</s:text>
		<br />
		<a href="http://help.picsorganizer.com/display/contractors/Facilities+List+Maintenance" class="help" target="_BLANK">
			<s:text name="ContractorFacilities.ClickForHelp" />
		</a>
	</div>
</s:if>
<s:else>
	<label>
		<s:text name="ContractorFacilities.ContractorFacilities.NumberOfLinkedFacilities" />:
	</label>
	${currentOperators.size()}
	<br />

	<s:if test="permissions.contractor || permissions.admin">
		<s:if test="contractor.accountLevel.bidOnly">
			<b><s:text name="ContractorFacilities.UpgradeFeeIncrease" /></b>.
            <br />
		</s:if>

		<s:if test="contractor.hasUpgrade">
			<table>
				<tr>
					<td colspan="4">
						<label><s:text name="ContractorFacilities.AnnualMembership" /></label>
					</td>
				</tr>

				<s:iterator value="contractor.fees.keySet()" var="feeClass">
					<s:if test="!contractor.fees.get(#feeClass).newLevel.free && #feeClass.membership">
						<tr>
							<td colspan="2">
								<s:property value="contractor.fees.get(#feeClass).newLevel.fee" />:&nbsp;
							</td>
							<td class="right">
								<s:property value="contractor.country.currency.symbol" />
								<s:property value="contractor.fees.get(#feeClass).newAmount" />
							</td>
							<td>
								&nbsp;<s:property value="contractor.currency" />
							</td>
						</tr>
					</s:if>
				</s:iterator>

				<tr>
					<td class="left">
						<s:if test="contractor.accountLevel.full">
							<a href="${contractor_pricing}" rel="facebox" class="ext">
								<s:text name="ContractorFacilities.ViewPricing" />
							</a>
						</s:if>
					</td>
					<td>
						<s:text name="ContractorFacilities.Total" />
						:
					</td>
					<td class="right">
						<s:property value="contractor.country.currency.symbol" />
						<s:property value="contractor.newMembershipAmount" />
					</td>
					<td>
						&nbsp;<s:property value="contractor.currency" />
					</td>
				</tr>
			</table>
		</s:if>
		<s:elseif test="!contractor.hasFreeMembership">
			<table>
				<tr>
					<td colspan="4">
						<label><s:text name="ContractorFacilities.AnnualMembership" /></label>
					</td>
				</tr>

				<s:iterator value="contractor.fees.keySet()" var="feeClass">
					<s:if
						test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
						<tr>
							<td colspan="2">
								<s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:&nbsp;
							</td>
							<td class="right">
								<s:property value="contractor.country.currency.symbol" />
								<s:property value="contractor.fees.get(#feeClass).currentAmount" />
							</td>
							<td>
								&nbsp;<s:property value="contractor.currency" />
							</td>
						</tr>
					</s:if>
				</s:iterator>

				<tr>
					<td class="left">
						<s:if test="contractor.accountLevel.full">
							<a href="${contractor_pricing}" rel="facebox" class="ext">
								<s:text name="ContractorFacilities.ViewPricing" />
							</a>
						</s:if>
					</td>
					<td>
						<s:text name="ContractorFacilities.Total" />:
					</td>
					<td class="right">
						<s:property value="contractor.country.currency.symbol" />
						<s:property value="contractor.currentMembershipAmount" />
					</td>
					<td>
						&nbsp;<s:property value="contractor.currency" />
					</td>
				</tr>
			</table>
		</s:elseif>
	</s:if>

	<s:if test="currentOperators.size() > 1 && contractor.status.pending">
		<div class="alert" style="width: 80%">
			<s:text name="ContractorFacilities.IndicatedRequestedBy" />
			<br />
			<label>
				<s:text name="ContractorFacilities.Requestedby" />:
			</label>

			<s:select
				headerKey=""
				headerValue="- %{getText('RequestNewContractor.header.SelectAnOperator')} -"
				id="requested_by_operator"
				list="currentOperators"
				listKey="operatorAccount.id"
				listValue="operatorAccount.name"
				value="contractor.requestedBy.id" />
		</div>
	</s:if>

	<s:if test="trialContractor">
		<div class="info">
			<a href="javascript:;" class="picsbutton" id="switch_to_trial_account" data-contractor="${contractor.id}">
				<s:text name="ContractorFacilities.SwitchToBid" />
			</a>

			<span class="block" style="position: relative;">
				<a class="whatsthis" href="javascript:;">
					<img src="images/help.gif" height="15" width="15">
					<span class="hoverhelp" style="bottom: 20px; left: -100px;">
						<s:text name="ContractorFacilities.SwitchToBid" />
					</span>
				</a>
			</span>
			<br />
			<s:text name="ContractorFacilities.SwitchToBid" />
		</div>
	</s:if>

	<div class="info">
		<s:text name="ContractorFacilities.HelpWithAddingOrRemoving">
			<s:param value="%{getText('RegistrationSuperEliteSquadronPhone')}" />
		</s:text>
	</div>

	<table class="report" id="assigned_operators">
		<thead>
			<tr>
				<th>
					<s:text name="global.Flag" />
				</th>
				<th>
					<s:text name="global.Operator" />
				</th>
				<th>
					<s:text name="global.WaitingOn" />
				</th>

				<pics:permission perm="RemoveContractors">
					<th>
						<s:text name="global.Remove" />
						<br />
						<s:text name="global.Operator" />
					</th>
				</pics:permission>

				<s:if test="permissions.contractor && contractor.status.pending">
					<th>
						<s:text name="global.Remove" />
						<br />
						<s:text name="global.Operator" />
					</th>
				</s:if>
			</tr>
		</thead>
		<tbody>
			<s:iterator value="currentOperators">
				<tr>
					<s:url action="ContractorFlag" var="contractor_flag">
						<s:param name="id">
							${contractor.id}
						</s:param>
						<s:param name="opID">
							${operatorAccount.id}
						</s:param>
					</s:url>
					<s:if test="contractor.status.activeDemo">
						<td>
							<s:a href="%{contractor_flag}">
								<s:property value="flagColor.smallIcon" escape="false" />
							</s:a>
						</td>
						<td class="account<s:property value="operatorAccount.status" />">
							<s:a href="%{contractor_flag}">
								<s:property value="operatorAccount.name" />
							</s:a>
						</td>
						<td>
							<s:text name="%{waitingOn.i18nKey}" />
						</td>
					</s:if>
					<s:else>
						<td>
							<s:text name="global.NA" />
						</td>
						<td>
							<s:property value="operatorAccount.name" />
						</td>
						<td>
							<s:text name="global.Contractor" />
						</td>
					</s:else>

					<pics:permission perm="RemoveContractors">
						<td>
							<a
								href="javascript:;"
								class="remove"
								data-contractor="${contractor.id}"
								data-operator="${operatorAccount.id}">
								<s:text name="global.Remove" />
							</a>
						</td>
					</pics:permission>

					<s:if test="permissions.contractor && contractor.status.pending">
						<td>
							<a
								href="javascript:;"
								class="remove"
								data-contractor="${contractor.id}"
								data-operator="${operatorAccount.id}">
								<s:text name="global.Remove" />
							</a>
						</td>
					</s:if>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:else>