<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <th>Contractor</th>
	    <td>Created</td>
	    <td>Comp%</td>
	</tr>
	</thead>
	<s:iterator value="pendingWelcomeCalls">
	<tr>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('name')"/></a></td>
		<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
		<td class="right"><s:property value="get('percentComplete')"/>%</td>
	</tr>
	</s:iterator>
</table>