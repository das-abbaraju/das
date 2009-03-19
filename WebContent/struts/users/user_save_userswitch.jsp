<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="pics" uri="pics-taglib"%>

<s:include value="../actionMessages.jsp" />

<table class="report">
	<thead>
		<tr>
			<th colspan="2">
				Users Who Can Switch To This Group
			</th>
		</tr>
	</thead>
	<tbody> 
		<s:iterator value="user.switchFroms">
			<tr>
				<td><a href="?accountId=<s:property value="user.account.id"/>&user.id=<s:property value="user.id"/>&isActive=<s:property value="[1].isActive"/>&isGroup=<s:property value="[1].isGroup"/>"><s:property value="user.name"/></a></td>
				<td><a class="remove" href="#userSwitch" onclick="removeUserSwitch(<s:property value="user.id" />)">remove</a></td>
			</tr>
		</s:iterator>
		<tr>
			<td colspan="2">
				<input type="text" name="userSwitchAdd" id="userSwitchAdd" size="50"/>
				<div id="userSwitchAutocomplete" class="autocomplete"></div>
			</td>
		</tr>
	</tbody>
</table>