<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
		<td>Contractor</td>
		<td>PQF Completed Date</td>
	</tr>
	</thead>
	<s:if test="pqfVerifications.size() > 0">
		<s:iterator value="pqfVerifications" status="stat">
			<tr>
				<td><a href="VerifyView.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a></td>
				<td><s:date name="get('completedDate')" format="M/d/yy" /></td>
			</tr>
		</s:iterator>
	</s:if>
	<s:else>
		<tr><td colspan="2" class="center"> No PQF verifications currently</td></tr>
	</s:else>
</table>