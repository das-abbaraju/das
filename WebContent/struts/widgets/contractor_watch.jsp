<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<td>Contractor</td>
			<td>Activity</td>
			<td>Date</td>
		</tr>
	</thead>
	<s:iterator value="data" status="stat">
		<tr>
			<td><s:property value="get('name')" /></td>
			<td>
				<s:if test="get('url').length() > 0"><a href="<s:property value="get('url')" />"><s:property value="get('body')" /></a></s:if>
				<s:else><s:property value="get('body')" /></s:else>
			</td>
			<td><span title="<s:date name="get('activityDate')" nice="true" />"><s:date name="get('activityDate')" format="MM/dd/yyyy HH:mm:ss" /></span></td>
		</tr>
	</s:iterator>
</table>
