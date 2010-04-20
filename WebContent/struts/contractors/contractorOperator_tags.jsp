<%@ taglib prefix="s" uri="/struts-tags"%>
<table class="report" style="position: static">
	<s:iterator value="contractor.operatorTags">
		<s:if test="tag.active">
			<s:if test="tag.operator.id == permissions.accountId || (permissions.corporateParent.contains(tag.operator.id) && tag.inheritable) ">
				<tr>
					<td><s:property value="tag.tag" /></td>
					<td><img src="images/cross.png" width="18" height="18" /><a
							href="#" onclick="javascript:return removeTag(<s:property value="id"/>);">Remove</a>
					</td>
				</tr>
			</s:if>
		</s:if>
	</s:iterator>
	<s:if test = "operatorTags.size() > 0 ">
		<tr>
			<td colspan="2"><s:select id="tagName" list="operatorTags" listKey="id" listValue="tag" headerKey="0" headerValue="- Operator Tag -"/><input
			type="button" onclick="javascript: return addTag();" value="Add"></td>
		</tr>
	</s:if>
</table>
