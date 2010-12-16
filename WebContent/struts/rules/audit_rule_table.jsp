<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="pics" uri="pics-taglib"%>
<s:if test="columns.size() > 0">
	<table class="report">
		<thead>
			<s:iterator value="columns">
				<th colspan="<s:property value="colspan" />"><s:property value="name" /></th>
			</s:iterator>
		</thead>
		<tbody>
			<s:iterator value="mappedRules" id="rule">
				<tr>
					<s:iterator value="columns" id="column">
						<s:if test="#rule.get(#column).size() > 0">
							<s:iterator value="#rule.get(#column)">
								<td><s:property /></td>
							</s:iterator>
						</s:if>
						<s:else>
							<td colspan="<s:property value="#column.colspan" />">*</td>
						</s:else>
					</s:iterator>
					<td><a href="#" id="search"></a></td>
				</tr>
			</s:iterator>
		</tbody>
	</table>
</s:if>
<s:else>
	No rules found for this <s:property value="type" />
</s:else>