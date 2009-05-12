<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<s:iterator value="operatorAccount.names">
		<tr>
			<td><s:property value="name" /></td>
			<td><a class="remove"
				href="#" onclick="javascript:return removeName(<s:property value="id"/>);">Remove</a></td>
		</tr>
	</s:iterator>
	<tr>
		<td colspan="2"><s:textfield id="legalName" name="name"/><input
			type="button" onclick="javascript: return addName();" value="Add"></td>
	</tr>
</table>
