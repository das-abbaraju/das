<%@ taglib prefix="s" uri="/struts-tags"%>
<tr class="group<s:if test="#shaded">Shaded</s:if>">
	<td class="right" width="60px"><s:property value="category.number"/>.<s:property value="subCategory.number"/>.<s:property value="number"/>&nbsp;&nbsp;</td>
	<td class="question"><s:property value="question" escape="false"/>
	</td>
</tr>
<s:if test="requirement.length() > 0">
	<tr>
		<td><span class="redMain">Req:</span></td>
		<td><div id="info"><s:property value="requirement"/></div></td>
	</tr>
</s:if>	