<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<table class="report">
	<thead>
		<th>Flag</th>
		<th>Operator Name</th>
		<th>Waiting On</th>
		<pics:permission perm="RemoveContractors">
			<th>Remove<br/> Operator</th>		
		</pics:permission>
	</thead>
	<tbody>
<s:sort source="contractor.operators" comparator="conOpComparator">
<s:iterator>
	<s:if test="(permissions.operator && (permissions.accountId == operatorAccount.id ) ) || permissions.contractor || permissions.picsEmployee || doCorporateCheck(top)">
	<tr>
		<td>
			<s:if test="permissions.picsEmployee || permissions.corporate || permissions.contractor">
				<s:url id="flagUrl" action="ContractorFlag">
					<s:param name="id" value="%{contractor.id}"/>
					<s:param name="opID" value="%{operatorAccount.id}"/>
				</s:url>
				<s:a href="%{flagUrl}"><img src="images/icon_<s:property value="flag.flagColor.name().toLowerCase()"/>Flag.gif" width="12" height="15"></s:a>
			</s:if>
			<s:else>
				<img src="images/icon_<s:property value="flag.flagColor.name().toLowerCase()"/>Flag.gif" width="12" height="15">
			</s:else>
		</td>
		<td>
			<s:if test="permissions.picsEmployee || permissions.corporate || permissions.contractor">
				<s:url id="opUrl" action="ContractorFlag">
					<s:param name="id" value="%{contractor.id}"/>
					<s:param name="opID" value="%{operatorAccount.id}"/>
				</s:url>
				<s:a href="%{opUrl}"><s:property value="operatorAccount.name"/></s:a>
			</s:if>
			<s:else>
				<s:property value="operatorAccount.name"/>
			</s:else>
		</td>
		<td>
			<s:property value="flag.waitingOn.name()"/>
		</td>

		<pics:permission perm="RemoveContractors">
			<td><a href="#" onclick="javascript: return removeOperator( <s:property value="contractor.id"/>, <s:property value="operatorAccount.id"/> );">Remove</a></td>		
		</pics:permission>
	</tr>
	</s:if>
</s:iterator>
</s:sort>
	</tbody>
	<s:if test="permissions.admin || permissions.isCorporate">
	<tfoot>
		
		<tr><td>&nbsp;</td><td>Total Operator Count</td><td><s:property value="contractor.operators.size()"/></td><td>&nbsp;</td></tr>
	</tfoot>
	</s:if>
	
</table>