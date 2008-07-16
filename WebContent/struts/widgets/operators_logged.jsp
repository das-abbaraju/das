<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <td>Time</td>
	    <td>Contact</td>
	    <td>Name</td>
	</tr>
	</thead>
	<s:iterator value="loggedOperators">
		<tr>
			<td><nobr><s:date name="lastLogin" format="MM/dd hh:mm"/></nobr></td>
			<td><s:property value="name"/></td>
			<td><a href="accounts_edit_operator.jsp?id=<s:property value="account.id"/>"><s:property value="account.name"/></td>
		</tr>
	</s:iterator>
</table>
