<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="currentOperators.size() == 0">
	<div class="alert" style="width: 80%;">This account is not linked to any operators. Use the search tool to the right to find all of the facilities at which <s:property value="contractor.name"/> works or will work. 
	<br/>
	<a href="http://help.picsauditing.com/wiki/Facilities_List_Maintenance#Adding_Facilities" class="help" target="_BLANK">Click here for help.</a></div>
</s:if>
<s:else>
<label><s:text name="%{scope}.ContractorFacilities.NumberOfLinkedFacilities" />:</label> <s:property value="currentOperators.size()" /><br />
<s:if test="permissions.contractor || permissions.admin">
	<s:if test="contractor.acceptsBids">
		<b>If you are awarded a bid or decide to convert from list only to a full membership your new membership fee will increase</b>.<br />
	</s:if>

	<s:if test="contractor.currentMembership.size == 0 || contractor.currentMembershipAmount < contractor.newMembershipAmount"> 
		<table>
			<tr><td colspan="4"><label>Annual Membership</label></td></tr>
			<s:iterator value="contractor.newMembership">
				<tr><td colspan="2"><s:property value="fee" />:</td><td class="right">$<s:property value="getAmount(contractor)" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
			</s:iterator>
			<tr><td class="left"><a onclick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=420,height=420'); return false;" href="#" title="opens in new window">View Pricing</a></td><td>Total:</td><td class="right">$<s:property value="contractor.newMembershipAmount"/></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
		</table>
	</s:if>
	<s:else>
		<table>
			<tr><td colspan="4"><label>Annual Membership</label></td></tr>
			<s:iterator value="contractor.currentMembership">
				<tr><td colspan="2"><s:property value="fee" />:</td><td class="right">$<s:property value="getAmount(contractor)" /></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
			</s:iterator>
			<tr><td class="left"><a onclick="window.open('con_pricing.jsp','name','toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=420,height=420'); return false;" href="#" title="opens in new window">View Pricing</a></td><td>Total:</td><td class="right">$<s:property value="contractor.currentMembershipAmount"/></td><td>&nbsp;<s:property value="contractor.currency"/></td></tr>
		</table>
	</s:else>
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
			onclick="javascript: return changeToTrialAccount( <s:property value="contractor.id"/>);">Switch To List Only Account</a> 
			<span class="block" style="position: relative;">
				<a class="whatsthis" href="#"><img src="images/help.gif" height="15" width="15">
					<span class="hoverhelp" style="bottom: 20px; left: -100px;">
					By switching to a List Only account you will only be able to complete an introductory process for the 
					facilities/operators that you are listed with.
			</span></a></span><br/>
			By switching to a List Only account you will only be able to complete an introductory process for
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
