<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<thead>
	<tr>
	    <td>Time</td>
	    <td>Contact</td>
	    <td>Name</td>
	</tr>
	</thead>
	<s:iterator value="loggedContractors">
		<tr>
			<td><nobr><s:date name="lastLogin" format="MM/dd hh:mm"/></nobr></td>
			<td><s:property value="primaryContact.name"/></td>
			<td><a href="ContractorView.action?id=<s:property value="id"/>"><s:property value="name"/></a></td>
		</tr>
	</s:iterator>
</table>
