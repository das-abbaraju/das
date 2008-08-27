<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<table class="report">
<thead>
	<tr>
		<th colspan="2">Group(s):</th>
	</tr>
</thead>
<tbody>
<s:iterator value="user.groups">
	<tr>
		<td><a href="#" onclick="showUser(<s:property value="group.id"/>); return false;"><s:property value="group.name"/></a></td>
		<td>&nbsp; <a href="#" onclick="removeGroup(<s:property value="userGroupID"/>); return false;">remove</a></td>
	</tr>
</s:iterator>
<s:iterator value="addableGroups">
	<tr>
		<td><a href="#" style="font-style: italic; color: red;" 
			onclick="showUser(<s:property value="id"/>); return false;"><s:property value="name"/></a></td>
		<td>&nbsp; <a href="#" style="font-style: italic; color: red;" 
			onclick="saveGroup(<s:property value="id"/>); return false;">add</a></td>
	</tr>
</s:iterator>
</tbody>
</table>
