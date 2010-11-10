<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="columnNames != null">
	<table class="report">
		<thead>
			<s:iterator value="columnNames">
				<th colspan="<s:property value="colspan" />"><s:property value="name" /></th>
			</s:iterator>
		</thead>
		<tbody>
			<s:iterator value="ruleIDs" id="r">
				<tr class="clickable" onclick="window.open('<s:property value="type" />RuleEditor.action?id=<s:property value="#r" />')">
					<s:iterator value="columnNames" id="c">
						<s:if test="ruleMap.get(#r, #c).size > 0">
							<s:iterator value="ruleMap.get(#r, #c)">
								<td><s:property /></td>
							</s:iterator>
						</s:if>
						<s:else>
							<td colspan="<s:property value="#c.colspan" />">*
								<s:property value="ruleMap.get(#r, #c)" />
								<s:property value="ruleMap.get(#r, #c).size()" />
							</td>
						</s:else>
					</s:iterator>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	No rules found for this <s:property value="type" />
</s:else>