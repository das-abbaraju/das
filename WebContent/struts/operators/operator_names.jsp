<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report">
	<s:iterator value="operatorAccount.names">
		<tr>
			<td><s:property value="name" /></td>
			<td><img src="images/cross.png" width="18" height="18" /><a
				href="#" onclick="javascript:return removeName(<s:property value="id"/>);">Remove</a></td>
		</tr>
	</s:iterator>
	<tr>
		<td colspan="2"><s:textfield id="legalName" name="name"/><input
			type="button" onclick="javascript: return addName();" value="Add"></td>
	</tr>
</table>
