<%@ taglib prefix="s" uri="/struts-tags"%>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>
<table class="report">
	<thead>
	<tr>
		<td></td>
	    <th><a href="javascript: changeOrderBy('form1','a.name');" >Contractor</a></th>
	    <th>Edit</th>
		<th class="right"><a href="javascript: changeOrderBy('form1','oldAmount');">Old Level</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','newAmount');">New Level</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','billingStatus');">State</a></th>
		<th class="center"><a href="javascript: changeOrderBy('form1','ccOnFile DESC');">CC</a></th>
		<th class="right"><a href="javascript: changeOrderBy('form1','creationDate');">Registered</a></th>	    
		<th class="right"><a href="javascript: changeOrderBy('form1','lastUpgradeDate');">Upgraded</a></th>	    
		<th class="right"><a href="javascript: changeOrderBy('form1','paymentExpires');">Renews</a></th>	    
	</tr>
	</thead>
	<tbody>
	<s:iterator value="data" status="stat">
		<tr>
			<td class="right"><s:property value="#stat.index + report.firstRowNumber" /></td>
			<td><a href="ContractorView.action?id=<s:property value="get('id')"/>" 
					rel="ContractorQuickAjax.action?id=<s:property value="get('id')"/>" 
					class="contractorQuick" title="<s:property value="get('name')" />"
					><s:property value="get('name')" /></a></td>
			<td><a href="BillingDetail.action?id=<s:property value="get('id')"/>" target="BILLING_DETAIL">Billing Detail</a></td>
			<td class="right">$<s:property value="get('oldAmount')"/></td>
			<td class="right">$<s:property value="get('newAmount')"/></td>
			<td><s:property value="get('billingStatus')"/></td>
			<td class="center">
				<s:if test="get('ccOnFile') == 1">
					Yes
				</s:if>
				<s:else>
					No
				</s:else>
			</td>
			<td class="right"><s:date name="get('creationDate')" format="M/d/yy"/></td>
			<td class="right"><s:date name="get('lastUpgradeDate')" format="M/d/yy"/></td>
			<td class="right"><s:date name="get('paymentExpires')" format="M/d/yy"/></td>
		</tr>
	</s:iterator>
	</tbody>
</table>
<div>
<s:property value="report.pageLinksWithDynamicForm" escape="false" />
</div>