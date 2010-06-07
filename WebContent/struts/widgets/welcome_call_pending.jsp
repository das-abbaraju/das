<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <th>Contractor</th>
	    <td>Created</td>
	    <td>Comp%</td>
	</tr>
	</thead>
	<s:iterator value="pendingWelcomeCalls" begin="0" end="%{pendingWelcomeCalls.size() > 9 ? 9 : pendingWelcomeCalls.size() - 1}">
	<tr>
		<td><a href="Audit.action?auditID=<s:property value="get('auditID')"/>"><s:property value="get('name')" /></a></td>
		<td class="center"><s:date name="get('createdDate')" format="M/d/yy" /></td>
		<td class="right"><s:property value="get('percentComplete')"/>%</td>
	</tr>
	</s:iterator>
	<s:if test="pendingWelcomeCalls.size() > 10">
		<tr>
			<td colspan="3" class="right">
				<a href="ReportAuditList.action?filter.auditStatus=Pending&filter.auditTypeID=9&filter.auditorId=<s:property value="csrID" />">
					Total number of welcome calls:
					<b><s:property value="pendingWelcomeCalls.size()" /></b>
				</a>
			</td>
		</tr>
	</s:if>
</table>