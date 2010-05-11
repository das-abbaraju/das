<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
		<td>Contractor</td>
		<td>CSR</td>
		<td>PQF Completed Date</td>
	</tr>
	</thead>
	<s:iterator value="pqfVerifications" status="stat">
		<tr>
			<td><a href="VerifyView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
			<td><s:property value="get('csr_name')"/></td>
			<td><s:date name="get('completedDate')" format="M/d/yy" /></td>
		</tr>
	</s:iterator>	
</table>