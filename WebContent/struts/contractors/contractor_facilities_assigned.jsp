<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="currentOperators.size() == 0">
	<div class="alert" style="width: 80%;"><s:text name="ContractorFacilities.NotLinked" ><s:param><s:property value="contractor.name"/></s:param></s:text> 
	<br/>
	<a href="http://help.picsauditing.com/wiki/Facilities_List_Maintenance#Adding_Facilities" class="help" target="_BLANK"><s:text name="ContractorFacilities.ClickForHelp" /></a></div>
</s:if>
<s:else>
<label><s:text name="%{scope}.ContractorFacilities.NumberOfLinkedFacilities" />:</label> <s:property value="currentOperators.size()" /><br />
<s:if test="permissions.contractor || permissions.admin">
	<s:if test="contractor.accountLevel.bidOnly">
		<b><s:text name="ContractorFacilities.UpgradeFeeIncrease" /></b>.<br />
	</s:if>

	<s:if test="contractor.hasUpgrade"> 
		<table>
			<tr><td colspan="4"><label><s:text name="ContractorFacilities.AnnualMembership" /></label></td></tr>
			<s:iterator value="contractor.fees.keySet()" var="feeClass">
				<s:if test="!contractor.fees.get(#feeClass).newLevel.free && #feeClass.membership">
					<tr><td colspan="2"><s:property value="contractor.fees.get(#feeClass).newLevel.fee" />:&nbsp;</td><td class="right"><s:property value="contractor.currencyCode.symbol" /><s:property value="contractor.fees.get(#feeClass).newAmount" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
				</s:if>
			</s:iterator>
			<tr><td class="left"><s:if test="contractor.accountLevel.full">
				<a href="ContractorPricing.action?con=<s:property value="contractor.id" />" rel="facebox" class="ext"><s:text name="ContractorFacilities.ViewPricing" /></a>
				</s:if></td>
				<td><s:text name="ContractorFacilities.Total" />:</td><td class="right"><s:property value="contractor.currencyCode.symbol" /><s:property value="contractor.newMembershipAmount"/></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
		</table>
	</s:if>
	<s:elseif test="!contractor.hasFreeMembership">
		<table>
			<tr><td colspan="4"><label><s:text name="ContractorFacilities.AnnualMembership" /></label></td></tr>
			<s:iterator value="contractor.fees.keySet()" var="feeClass">
				<s:if test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
					<tr><td colspan="2"><s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:&nbsp;</td><td class="right"><s:property value="contractor.currencyCode.symbol" /><s:property value="contractor.fees.get(#feeClass).currentAmount" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
				</s:if>
			</s:iterator>
			<tr><td class="left"><s:if test="contractor.accountLevel.full">
				<a href="ContractorPricing.action?con=<s:property value="contractor.id" />" rel="facebox" class="ext"><s:text name="ContractorFacilities.ViewPricing" /></a>
				</s:if></td>
				<td><s:text name="ContractorFacilities.Total" />:</td><td class="right"><s:property value="contractor.currencyCode.symbol" /><s:property value="contractor.currentMembershipAmount"/></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
		</table>
	</s:elseif>
</s:if>

<s:if test="currentOperators.size() > 1 && contractor.status.pending">
	<div class="alert" style="width:80%">
		<s:text name="ContractorFacilities.IndicatedRequestedBy" /><br/>
		<label><s:text name="ContractorFacilities.Requestedby" />:</label> 
			<s:select list="currentOperators" listKey="operatorAccount.id" listValue="operatorAccount.name" 
				headerKey="" headerValue="- Select An Operator -" value="contractor.requestedBy.id" 
				onchange="setRequestedBy(%{contractor.id}, this.value)" id="requestedBySelector"/>
	</div>
</s:if>

<s:if test="trialContractor">
	<div class="info" style="80%">
		<a href="#" class="picsbutton" id="bidonly"
			onclick="javascript: return changeToTrialAccount( <s:property value="contractor.id"/>);"><s:text name="ContractorFacilities.SwitchToBid" /></a> 
			<span class="block" style="position: relative;">
				<a class="whatsthis" href="#"><img src="images/help.gif" height="15" width="15">
					<span class="hoverhelp" style="bottom: 20px; left: -100px;">
					<s:text name="ContractorFacilities.SwitchToBid" />
			</span></a></span><br/>
			<s:text name="ContractorFacilities.SwitchToBid" /></div>
</s:if>

<div class="info">
	<s:text name="ContractorFacilities.HelpWithAddingOrRemoving" ><s:param value="%{getText('RegistrationSuperEliteSquadronPhone')}" /></s:text>
</div>		

<table class="report">
	<thead>
		<tr>
			<th><s:text name="global.Flag"/></th>
			<th><s:text name="global.Operator"/></th>
			<th><s:text name="global.WaitingOn"/></th>
			<pics:permission perm="RemoveContractors">
				<th><s:text name="global.Remove"/><br />
				<s:text name="global.Operator"/></th>
			</pics:permission>
			<s:if test="permissions.contractor && contractor.status.pending">
				<th><s:text name="global.Remove"/><br />
				<s:text name="global.Operator"/></th>
			</s:if>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="currentOperators">
			<tr id="operator_<s:property value="operatorAccount.id"/>">
				<s:if test="contractor.status.activeDemo">
					<td><s:url id="flagUrl" action="ContractorFlag">
						<s:param name="id" value="%{contractor.id}" />
						<s:param name="opID" value="%{operatorAccount.id}" />
					</s:url><s:a href="%{flagUrl}">
						<s:property value="flagColor.smallIcon" escape="false" />
					</s:a></td>
					<td class="account<s:property value="operatorAccount.status" />">
						<s:url id="opUrl" action="ContractorFlag">
							<s:param name="id" value="%{contractor.id}" />
							<s:param name="opID" value="%{operatorAccount.id}" />
						</s:url>
						<s:a href="%{opUrl}"><s:property value="operatorAccount.name" /></s:a>
					</td>
					<td><s:text name="%{waitingOn.i18nKey}"/></td>
				</s:if>
				<s:else>
					<td><s:text name="global.NA"/></td>
					<td><s:property value="operatorAccount.name"/></td>
					<td><s:text name="global.Contractor"/></td>
				</s:else>

				<pics:permission perm="RemoveContractors">
					<td><a id="facility_<s:property value="operatorAccount.id"/>" href="#" class="remove"
						onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );"><s:text name="global.Remove"/></a></td>
				</pics:permission>
				<s:if test="permissions.contractor && contractor.status.pending">
					<td><a id="facility_<s:property value="operatorAccount.id"/>" href="#" class="remove"
						onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );"><s:text name="global.Remove"/></a></td>
				</s:if>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:else>
