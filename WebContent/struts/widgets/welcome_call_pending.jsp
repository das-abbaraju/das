<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <th>Contractor</th>
	    <td>Created</td>
	    <td>Comp%</td>
	</tr>
	</thead>
	<tbody>
		<s:if test="pendingWelcomeCalls.size() > 0">
			<s:iterator value="pendingWelcomeCalls" begin="0" end="%{pendingWelcomeCalls.size() > 9 ? 9 : pendingWelcomeCalls.size() - 1}">
			<tr>
				<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:text name="%{get('atype.name')}" /></a></td>
				<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
				<td class="right"><s:property value="get('percentComplete')"/>%</td>
			</tr>
			</s:iterator>
		</s:if>
		<s:else>
			<tr><td colspan="3">No pending welcome calls</td></tr>
		</s:else>
		<s:if test="pendingWelcomeCalls.size() > 10">
			<tr>
				<td colspan="3" class="right">
					Total number of welcome calls:
					<b><s:property value="pendingWelcomeCalls.size()" /></b>
				</td>
			</tr>
		</s:if>
	</tbody>
</table>