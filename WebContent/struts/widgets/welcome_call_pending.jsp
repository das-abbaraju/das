<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <th>Contractor</th>
	    <td>Created</td>
	    <td>Comp%</td>
	</tr>
	</thead>
	<s:iterator value="pendingWelcomeCalls" begin="0" end="9">
	<tr>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('name')"/></a></td>
		<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
		<td class="right"><s:property value="get('percentComplete')"/>%</td>
	</tr>
	</s:iterator>
	<s:if test="pendingWelcomeCalls.size() > 10">
		<tr>
			<td colspan="3" class="right">Total number of welcome calls:
			<b><s:property value="pendingWelcomeCalls.size()" /></b></td>
		</tr>
	</s:if>
</table>