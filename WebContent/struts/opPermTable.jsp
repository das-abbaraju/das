<%@ taglib prefix="s" uri="/struts-tags"%>

<link rel="stylesheet" href="css/reports.css" />

<table class="report">
<thead>
<tr>
	<td>OpPerm</td>
	<td>Description</td>
	<td>HelpText</td>
	<td>UsesView</td>
	<td>UsesEdit</td>
	<td>UsesDelete</td>
	<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
	<td>ForAdmin</td>
	<td>ForContractor</td>
	<td>ForOperator</td>
</tr>
</thead>

<s:iterator value="@com.picsauditing.access.OpPerms@values()">
<tr>
	<td>
		<s:property/>
	</td>
	<td>
		<s:property value="description"/>
	</td>
	<td>
		<s:property value="helpText"/>
	</td>
	<td>
		<s:property value="usesView()"/>
	</td>
	<td>
		<s:property value="usesEdit()"/>
	</td>
	<td>
		<s:property value="usesDelete()"/>
	</td>
	<td>
		
	</td>
	<td>
		<s:property value="forAdmin"/>
	</td>
	<td>
		<s:property value="forContractor"/>
	</td>
	<td>
		<s:property value="forOperator"/>
	</td>
</tr>
</s:iterator>

</table>
