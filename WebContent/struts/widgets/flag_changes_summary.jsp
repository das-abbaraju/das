<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
		<tr>
			<th>Flag Changes</th>
			<th>Operator</th>
		</tr>
	</thead>
	<s:iterator value="flagSummary">
		<tr>
			<td class="center"><s:property value="get('flagChanges')"/></td>
			<td>
				<a href="FacilitiesEdit.action?id=<s:property value="get('id')"/>"><s:property value="get('name')"/></a>
			</td>
		</tr>
	</s:iterator>
</table>
