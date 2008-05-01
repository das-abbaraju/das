<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
		<th>Contractor</th>
		<th>Date</th>
		</tr>
	</thead>
	<s:iterator value="newContractors">
		<tr>
			<td><a href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
			<td class="center"><s:date name="accountDate" format="M/d/yy" /></td>
		</tr>
	</s:iterator>
</table>
