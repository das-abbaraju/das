<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:if test="currentOperators.size() == 0">
	<div id="alert">This account is not linked to any operators. Use the search tool to the right to find all of the facilities at which <s:property value="contractor.name"/> works or will work.</div>
</s:if>
<s:else>
<label># of Linked Facilities:</label> <s:property value="currentOperators.size()" /><br />
<s:if test="permissions.contractor || permissions.admin">
	<s:if test="contractor.paymentExpires != null">
		<label>Next Payment Due:</label> <s:date name="contractor.paymentExpires" format="M/d/yy" /><br />
	</s:if>	
	<s:if test="contractor.membershipLevel.amount > 0"> 
		<label>Current Membership Level:</label> $<s:property value="contractor.membershipLevel.amount" /> per year<br />
	</s:if>
	<label>New Membership Level:</label> $<s:property value="contractor.newMembershipLevel.amount" /> per year<br />
	<s:property value="contractor.newMembershipLevel.fee" />
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
		</tr>
	</thead>
	<tbody>
		<s:iterator value="currentOperators">
			<tr>
				<td><s:if test="contractor.activeB"><s:url id="flagUrl" action="ContractorFlag">
					<s:param name="id" value="%{contractor.id}" />
					<s:param name="opID" value="%{operatorAccount.id}" />
				</s:url></s:if><s:a href="%{flagUrl}">
					<s:property value="flag.flagColor.smallIcon" escape="false" />
				</s:a></td>
				<td><s:if test="contractor.activeB"><s:url id="opUrl" action="ContractorFlag">
					<s:param name="id" value="%{contractor.id}" />
					<s:param name="opID" value="%{operatorAccount.id}" />
				</s:url></s:if><s:a href="%{opUrl}">
					<s:property value="operatorAccount.name" />
				</s:a></td>
				<td><s:property value="flag.waitingOn.name()" /></td>

				<pics:permission perm="RemoveContractors">
					<td><a href="#"
						onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );">Remove</a></td>
				</pics:permission>
			</tr>
		</s:iterator>
	</tbody>
</table>
</s:else>
