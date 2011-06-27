<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="contractor.needsToIndicateCompetitor">
	<div class="info"><p><strong>Are you a member of CanQual or ComplyWorks?</strong></p>
		<span>
			<a href="#" class="picsbutton positive"
				onclick="javascript: return setCanadianCompetitorAnswer(<s:property value="contractor.id"/>,true);">Yes</a>
			<a href="#" class="picsbutton negative"
				onclick="javascript: return setCanadianCompetitorAnswer(<s:property value="contractor.id"/>,false);">No</a>
		</span>
	</div>
</s:if>
<s:if test="currentOperators.size() == 0">
	<div class="alert" style="width: 80%;">This account is not linked to any operators. Use the search tool to the right to find all of the facilities at which <s:property value="contractor.name"/> works or will work. 
	<br/>
	<a href="http://help.picsauditing.com/wiki/Facilities_List_Maintenance#Adding_Facilities" class="help" target="_BLANK">Click here for help.</a></div>
</s:if>
<s:else>
<label><s:text name="%{scope}.ContractorFacilities.NumberOfLinkedFacilities" />:</label> <s:property value="currentOperators.size()" /><br />
<s:if test="permissions.contractor || permissions.admin">
	<s:if test="contractor.acceptsBids">
		<b>If you are awarded a bid or decide to convert from bid only to a full membership your new membership fee will increase</b>.<br />
	</s:if>

	<s:if test="contractor.hasUpgrade"> 
		<table>
			<tr><td colspan="4"><label>Annual Membership</label></td></tr>
			<s:iterator value="contractor.fees.keySet()" var="feeClass">
				<s:if test="!contractor.fees.get(#feeClass).newLevel.free && #feeClass.membership">
					<tr><td colspan="2"><s:property value="contractor.fees.get(#feeClass).newLevel.fee" />:&nbsp;</td><td class="right"><s:property value="contractor.currencyCode.icon" /><s:property value="contractor.fees.get(#feeClass).newAmount" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
				</s:if>
			</s:iterator>
			<tr><td class="left"><s:if test="contractor.accountLevel.full">
				<a onclick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=420,height=420'); return false;" href="#" title="opens in new window">View Pricing</a>
				</s:if></td>
				<td>Total:</td><td class="right"><s:property value="contractor.currencyCode.icon" /><s:property value="contractor.newMembershipAmount"/></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
		</table>
	</s:if>
	<s:elseif test="!contractor.hasFreeMembership">
		<table>
			<tr><td colspan="4"><label>Annual Membership</label></td></tr>
			<s:iterator value="contractor.fees.keySet()" var="feeClass">
				<s:if test="!contractor.fees.get(#feeClass).currentLevel.free && #feeClass.membership">
					<tr><td colspan="2"><s:property value="contractor.fees.get(#feeClass).currentLevel.fee" />:&nbsp;</td><td class="right"><s:property value="contractor.currencyCode.icon" /><s:property value="contractor.fees.get(#feeClass).currentAmount" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
				</s:if>
			</s:iterator>
			<tr><td class="left"><s:if test="contractor.accountLevel.full">
				<a onclick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=420,height=420'); return false;" href="#" title="opens in new window">View Pricing</a>
				</s:if></td>
				<td>Total:</td><td class="right"><s:property value="contractor.currencyCode.icon" /><s:property value="contractor.currentMembershipAmount"/></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
		</table>
	</s:elseif>
</s:if>

<s:if test="currentOperators.size() > 1 && contractor.status.pendingDeactivated">
	<div class="alert" style="width:80%">
		You have selected more than one operator. Please indicate which operator initially requested your company to register with PICS. <br/>
		<label>Requested By:</label> 
			<s:select list="currentOperators" listKey="operatorAccount.id" listValue="operatorAccount.name" 
				headerKey="" headerValue="- Select An Operator -" value="contractor.requestedBy.id" 
				onchange="setRequestedBy(%{contractor.id}, this.value)" id="requestedBySelector"/>
	</div>
</s:if>

<s:if test="trialContractor">
	<div class="info" style="80%">
		<a href="#" class="picsbutton" id="bidonly"
			onclick="javascript: return changeToTrialAccount( <s:property value="contractor.id"/>);">Switch To Bid Only Account</a> 
			<span class="block" style="position: relative;">
				<a class="whatsthis" href="#"><img src="images/help.gif" height="15" width="15">
					<span class="hoverhelp" style="bottom: 20px; left: -100px;">
					By switching to a Bid Only account you will only be able to complete an introductory process for the 
					facilities/operators that you are listed with.
			</span></a></span><br/>
			By switching to a Bid Only account you will only be able to complete an introductory process for
			the facilities/operators that you are listed with.</div>
</s:if>

<table class="report">
	<thead>
		<tr>
			<th>Flag</th>
			<th>Operator Name</th>
			<th>Waiting On</th>
			<pics:permission perm="RemoveContractors">
				<th>Remove<br />
				Operator</th>
			</pics:permission>
			<s:if test="permissions.contractor && contractor.status.pendingDeactivated">
				<th>Remove<br />
				Operator</th>
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
					<td><s:property value="waitingOn.name()" /></td>
				</s:if>
				<s:else>
					<td>N/A</td>
					<td><s:property value="operatorAccount.name"/></td>
					<td>Contractor</td>
				</s:else>

				<pics:permission perm="RemoveContractors">
					<td><a id="facility_<s:property value="operatorAccount.id"/>" href="#" class="remove"
						onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );">Remove</a></td>
				</pics:permission>
				<s:if test="permissions.contractor && contractor.status.pendingDeactivated">
					<td><a id="facility_<s:property value="operatorAccount.id"/>" href="#" class="remove"
						onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );">Remove</a></td>
				</s:if>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:else>
